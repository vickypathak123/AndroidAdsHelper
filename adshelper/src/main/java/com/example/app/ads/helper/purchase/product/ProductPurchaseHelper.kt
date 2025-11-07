@file:Suppress("unused")

package com.example.app.ads.helper.purchase.product

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.getStringRes
import com.example.app.ads.helper.purchase.getCurrencySymbol
import com.example.app.ads.helper.purchase.utils.TimeLinePlanType
import com.example.app.ads.helper.utils.clearAll
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.isPurchaseHistoryLogEnable
import com.example.app.ads.helper.utils.logE
import com.example.app.ads.helper.utils.logI
import com.example.app.ads.helper.utils.logW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.roundToInt

object ProductPurchaseHelper {
    private val TAG: String = "AdMob_${javaClass.simpleName}"

    private var isBillingClientInitialized: Boolean = false

    // variable to track event time
    private var mLastEventTrackTime: Long = 0
    private const val MIN_DURATION = 1000

    private val lifeTimeProductKeyList: ArrayList<String> = ArrayList()
    private val subscriptionKeyList: ArrayList<String> = ArrayList()
    val PRODUCT_LIST: ArrayList<ProductInfo> = ArrayList()

    val checkIsAllPriceLoaded: Boolean get() = PRODUCT_LIST.isNotEmpty() && PRODUCT_LIST.all { it.formattedPrice.isNotEmpty() && it.formattedPrice != PurchaseHelperText.NOT_FOUND.value }

    private var mPurchaseListener: ProductPurchaseListener? = null // Callback for listen purchase states
    private var mBillingClient: BillingClient? = null // Object of BillingClient

    private var isConsumable: Boolean = false // Flag if purchase need to consume so user can buy again

    @JvmStatic
    val String.getProductInfo: ProductInfo? get() = PRODUCT_LIST.find { it.id.getSKU.equals(this, true) }

    private val Int.getPurchaseState: String
        get() {
            return when (this) {
                Purchase.PurchaseState.UNSPECIFIED_STATE -> PurchaseHelperText.UNSPECIFIED_STATE.value
                Purchase.PurchaseState.PURCHASED -> PurchaseHelperText.PURCHASED.value
                Purchase.PurchaseState.PENDING -> PurchaseHelperText.PENDING.value
                else -> PurchaseHelperText.UNKNOWN.value
            }
        }

    private val String.getPriceInDouble: Double
        get() {
            val finalPrice = if (this.isNotEmpty() && !this.equals(PurchaseHelperText.NOT_FOUND.value, true)) {
                // Remove non-numeric characters except for one decimal point
                val numericWithDot = this.replace(Regex("[^0-9.]"), "")
                val firstDotIndex = numericWithDot.indexOf('.')

                // Handle cases with multiple decimal points or no decimal point
                if (firstDotIndex != -1 && numericWithDot.lastIndexOf('.') > firstDotIndex) {
                    // Multiple decimal points: replace all but the first
                    numericWithDot.replace(
                        Regex("(\\.[0-9]+).*"),
                        "." + numericWithDot.substring(firstDotIndex + 1)
                    )
                } else {
                    // No decimal point: append ".0"
                    if (firstDotIndex == -1) {
                        numericWithDot + ".0"
                    } else {
                        numericWithDot
                    }
                }
            } else {
                "0.0"
            }

            return try {
                finalPrice.toDouble()
            } catch (e: NumberFormatException) {
                // Handle specific NumberFormatException messages (optional)
                if (e.message?.contains("multiple points") == true) {
                    // Log or report multiple decimal point error
                    Log.e("Price Parsing", "getPriceInDouble: Price string contained multiple decimal points: $finalPrice")
                }
                0.0
            } catch (e: Exception) {
                // Catch any other unexpected exceptions
                Log.e("Price Parsing", "getPriceInDouble: Unexpected error parsing price: $finalPrice", e)
                0.0
            }
        }


    val String.getSKU: String get() = this.takeIf { !this.contains(":".toRegex()) } ?: kotlin.run { this.split(":".toRegex()).first() }

    //<editor-fold desc="set Product Keys">
    internal fun setLifeTimeProductKey(vararg keys: String) {
        lifeTimeProductKeyList.clearAll()
        lifeTimeProductKeyList.addAll(keys.filter { it.isNotEmpty() })
    }

    internal fun setSubscriptionKey(vararg keys: String) {
        subscriptionKeyList.clearAll()
        subscriptionKeyList.addAll(keys.filter { it.isNotEmpty() })
    }

    fun addOtherLifeTimeProductKey(keys: String) {
        if (!lifeTimeProductKeyList.contains(keys)) {
            lifeTimeProductKeyList.add(keys)
        }
    }

    fun addOtherSubscriptionKey(keys: String) {
        if (!subscriptionKeyList.contains(keys)) {
            subscriptionKeyList.add(keys)
        }
    }
    //</editor-fold>

    //<editor-fold desc="init Billing Related Data">
    /**
     * initialization of Google Billing Client.
     *
     * @param fContext it's refers to your application, activity or fragment context.
     */
    fun initBillingClient(fContext: Context) {
        logE(tag = TAG, message = "initBillingClient call -> isBillingClientInitialized::$isBillingClientInitialized")

        if (!isBillingClientInitialized) {

            mBillingClient = BillingClient.newBuilder(fContext)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .enablePrepaidPlans()
                        .build()
                )
                .setListener { billingResult, purchases ->
                    if ((SystemClock.elapsedRealtimeNanos() - mLastEventTrackTime) < MIN_DURATION) {
                        return@setListener
                    } else {
                        mLastEventTrackTime = SystemClock.elapsedRealtime()

                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK -> {
                                CoroutineScope(Dispatchers.IO).launch {
                                    purchases?.let { purchasesList ->
                                        if (purchasesList.isNotEmpty()) {
                                            purchasesList.forEach { purchase ->
                                                handlePurchase(context = fContext, purchase = purchase)
                                            }
                                        }
                                    } ?: kotlin.run {
                                        logI(tag = TAG, message = "onPurchasesUpdated: =>> Response OK But Purchase List Null Found")
                                    }
                                }
                            }

                            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
                            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                            }

                            BillingClient.BillingResponseCode.USER_CANCELED -> fContext.toasts(messageId = R.string.error_user_canceled)

                            else -> fContext.toasts(messageId = R.string.error_billing_unavailable)
                        }

                        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                            logResponseCode(responseMsg = "onPurchasesUpdated: ", billingResult = billingResult)
                        }
                    }
                }
                .build()

            mBillingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    logI(tag = TAG, message = "onBillingServiceDisconnected: =>> DISCONNECTED")
                    isBillingClientInitialized = false
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    logResponseCode("onBillingSetupFinished: ", billingResult)
                    when (billingResult.responseCode) {
                        BillingClient.BillingResponseCode.OK,
                        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    isBillingClientInitialized = true
                                }
                                mPurchaseListener?.onBillingSetupFinished()
                            }
                        }
                    }
                }
            })
        } else {
            mPurchaseListener?.onBillingSetupFinished()
        }
    }

    /**
     * initialization of Product SKU data using Google Billing.
     *
     * @param fContext it's refers to your application, activity or fragment context.
     * @param onInitializationComplete Callback for data will be initialized.
     */
    fun initProductsKeys(fContext: Context, onInitializationComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            initProducts(
                context = fContext,
                onComplete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        initSubscription(
                            context = fContext,
                            onComplete = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    onInitializationComplete.invoke()
                                }
                            }
                        )
                    }
                }
            )
        }
    }

    private fun checkAnyPurchaseAvailable(context: Context, methodName: String, productType: String, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            mBillingClient?.let { billingClient ->
                if (billingClient.isReady) {
                    billingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(productType)
                            .build()
                    ) { billingResult, purchaseList ->
                        when (billingResult.responseCode) {
                            BillingClient.BillingResponseCode.OK,
                            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                                if (purchaseList.isNotEmpty()) {
                                    onPurchased(context, productType)
                                } else {
                                    onExpired(context, productType)
                                }
                            }

                            else -> {
                                onExpired(context, productType)
                                logResponseCode(
                                    responseMsg = "$methodName: ",
                                    billingResult = billingResult
                                )
                            }
                        }
                        onComplete.invoke()
                    }
                } else {
                    logE(TAG, "$methodName: =>> The billing client is not ready")
                    onComplete.invoke()
                }
            } ?: kotlin.run {
                logE(TAG, "$methodName: =>> The billing client is NULL")
                onComplete.invoke()
            }
        }
    }

    private fun initProducts(context: Context, onComplete: () -> Unit) {
        checkAnyPurchaseAvailable(
            context = context,
            methodName = "initProducts",
            productType = BillingClient.ProductType.INAPP,
            onComplete = {
                CoroutineScope(Dispatchers.IO).launch {
                    mBillingClient?.let { billingClient ->
                        if (billingClient.isReady) {
                            val params = QueryPurchasesParams.newBuilder()
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()

                            billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
                                // This block is the callback that executes when the query is complete.
                                // It runs on the main thread, so move heavy work to a coroutine if needed.
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                        purchasesList?.let { listOfOwnedProducts ->
                                            val idList = listOfOwnedProducts.flatMap { it.products } as ArrayList<String>

                                            // Your recursive call to the next init step
                                            initProducts(
                                                context = context,
                                                historyList = idList,
                                                onComplete = onComplete
                                            )

                                            // IMPORTANT: You must update logPurchaseHistory to accept List<Purchase>
                                            logPurchaseHistory(
                                                fMethodName = "initProducts",
                                                purchaseHistoryRecordList = listOfOwnedProducts
                                            )

                                        } ?: initProducts(
                                            context = context,
                                            historyList = ArrayList(),
                                            onComplete = onComplete
                                        )
                                    } else {
                                        // Handle error case
                                        logResponseCode("queryPurchasesAsync (in-app): ", billingResult)
                                        initProducts(
                                            context = context,
                                            historyList = ArrayList(),
                                            onComplete = onComplete
                                        )
                                    }
                                }
                            }
                        } else {
                            initProducts(
                                context = context,
                                historyList = ArrayList(),
                                onComplete = onComplete
                            )
                        }
                    } ?: kotlin.run {
                        initProducts(
                            context = context,
                            historyList = ArrayList(),
                            onComplete = onComplete
                        )
                    }
                }
            }
        )
    }

    private suspend fun initProducts(
        context: Context,
        historyList: java.util.ArrayList<String>,
        onComplete: () -> Unit
    ) {
        if (lifeTimeProductKeyList.isNotEmpty()) {
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    lifeTimeProductKeyList.map { productId ->
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId.getSKU)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    }
                )
                .build()

            initProductParams(
                context = context,
                methodName = "initProducts",
                params = params,
                productType = BillingClient.ProductType.INAPP,
                historyList = historyList,
                onComplete = onComplete
            )
        } else {
            onComplete.invoke()
        }
    }

    private fun initSubscription(context: Context, onComplete: () -> Unit) {
        checkAnyPurchaseAvailable(
            context = context,
            methodName = "initSubscription",
            productType = BillingClient.ProductType.SUBS,
            onComplete = {
                CoroutineScope(Dispatchers.IO).launch {
                    mBillingClient?.let { billingClient ->
                        if (billingClient.isReady) {
                            val historyParams = QueryPurchasesParams.newBuilder()
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()

                            billingClient.queryPurchasesAsync(historyParams).let { purchasesHistoryResult ->
                                if (purchasesHistoryResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    purchasesHistoryResult.purchasesList?.let { listOfHistoryProducts ->
                                        val idList = listOfHistoryProducts.flatMap { it.products } as ArrayList<String>
                                        initSubscription(
                                            context = context,
                                            historyList = idList,
                                            onComplete = onComplete
                                        )
                                        logPurchaseHistory(
                                            fMethodName = "initSubscription",
                                            purchaseHistoryRecordList = listOfHistoryProducts
                                        )
                                    } ?: kotlin.run {
                                        initSubscription(
                                            context = context,
                                            historyList = ArrayList(),
                                            onComplete = onComplete
                                        )
                                    }
                                } else {
                                    initSubscription(
                                        context = context,
                                        historyList = ArrayList(),
                                        onComplete = onComplete
                                    )
                                }
                            }
                        } else {
                            initSubscription(
                                context = context,
                                historyList = ArrayList(),
                                onComplete = onComplete
                            )
                        }
                    } ?: kotlin.run {
                        initSubscription(
                            context = context,
                            historyList = ArrayList(),
                            onComplete = onComplete
                        )
                    }
                }
            }
        )
    }

    private suspend fun initSubscription(
        context: Context,
        historyList: java.util.ArrayList<String>,
        onComplete: () -> Unit
    ) {
        if (subscriptionKeyList.isNotEmpty()) {
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    subscriptionKeyList.map { productId ->
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId.getSKU)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    }
                )
                .build()

            initProductParams(
                context = context,
                methodName = "initSubscription",
                params = params,
                productType = BillingClient.ProductType.SUBS,
                historyList = historyList,
                onComplete = onComplete
            )
        } else {
            onComplete.invoke()
        }
    }

    private suspend fun initProductParams(
        context: Context,
        methodName: String,
        params: QueryProductDetailsParams,
        productType: String,
        historyList: ArrayList<String> = ArrayList(),
        onComplete: () -> Unit
    ) {
        mBillingClient?.let { billingClient ->
            if (billingClient.isReady) {
                val productDetails = billingClient.queryProductDetails(params = params)
                if (productDetails.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetails.productDetailsList?.let { listOfProducts ->
                        listOfProducts.forEach { productDetail ->
                            val productID: String = productDetail.productId
                            if (PRODUCT_LIST.find { it.id.getSKU == productID } == null) {

                                val formattedPrice: String = when (productType) {
                                    BillingClient.ProductType.INAPP -> productDetail.oneTimePurchaseOfferDetails?.formattedPrice ?: PurchaseHelperText.NOT_FOUND.value

                                    BillingClient.ProductType.SUBS -> productDetail.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.filter {
                                        !it.formattedPrice.equals("Free", ignoreCase = true)
                                    }?.takeIf { it.isNotEmpty() }?.get(0)?.formattedPrice ?: PurchaseHelperText.NOT_FOUND.value

                                    else -> PurchaseHelperText.NOT_FOUND.value
                                }

                                val priceAmountMicros: Long = when (productType) {
                                    BillingClient.ProductType.INAPP -> productDetail.oneTimePurchaseOfferDetails?.priceAmountMicros ?: 0

                                    BillingClient.ProductType.SUBS -> productDetail.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.filter {
                                        !it.formattedPrice.equals("Free", ignoreCase = true)
                                    }?.takeIf { it.isNotEmpty() }?.get(0)?.priceAmountMicros ?: 0

                                    else -> 0
                                }

                                val priceCurrencyCode: String = when (productType) {
                                    BillingClient.ProductType.INAPP -> productDetail.oneTimePurchaseOfferDetails?.priceCurrencyCode ?: PurchaseHelperText.NOT_FOUND.value

                                    BillingClient.ProductType.SUBS -> productDetail.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.filter {
                                        !it.formattedPrice.equals("Free", ignoreCase = true)
                                    }?.takeIf { it.isNotEmpty() }?.get(0)?.priceCurrencyCode ?: PurchaseHelperText.NOT_FOUND.value

                                    else -> PurchaseHelperText.NOT_FOUND.value
                                }

                                val billingPeriod = when (productType) {
                                    BillingClient.ProductType.INAPP -> "OTP"

                                    BillingClient.ProductType.SUBS -> productDetail.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.filter {
                                        !it.formattedPrice.equals("Free", ignoreCase = true)
                                    }?.takeIf { it.isNotEmpty() }?.get(0)?.billingPeriod ?: PurchaseHelperText.NOT_FOUND.value

                                    else -> PurchaseHelperText.NOT_FOUND.value
                                }

                                val freeTrialPeriod = when (productType) {
                                    BillingClient.ProductType.SUBS -> {
                                        val historyIndex: Int = ((productDetail.subscriptionOfferDetails?.size ?: 1) - 1).takeIf { historyList.contains(productID) } ?: 0
                                        productDetail.subscriptionOfferDetails?.get(historyIndex)?.pricingPhases?.pricingPhaseList?.filter {
                                            it.formattedPrice.equals("Free", ignoreCase = true)
                                        }?.takeIf { it.isNotEmpty() }?.get(0)?.billingPeriod ?: PurchaseHelperText.NOT_FOUND.value
                                    }

                                    else -> PurchaseHelperText.NOT_FOUND.value
                                }


                                PRODUCT_LIST.add(
                                    ProductInfo(
                                        id = productID,
                                        formattedPrice = formattedPrice,
                                        priceAmountMicros = priceAmountMicros,
                                        priceCurrencyCode = priceCurrencyCode,
                                        priceCurrencySymbol = formattedPrice.getCurrencySymbol,
                                        actualBillingPeriod = billingPeriod,
                                        billingPeriod = billingPeriod.getFullBillingPeriod(context = context),
                                        actualFreeTrialPeriod = freeTrialPeriod,
                                        freeTrialPeriod = freeTrialPeriod.getFullBillingPeriod(context = context),
                                        offerFormattedPrice = PurchaseHelperText.NOT_FOUND.value,
                                        offerPriceAmountMicros = 0,
                                        offerPriceCurrencyCode = PurchaseHelperText.NOT_FOUND.value,
                                        offerPriceCurrencySymbol = PurchaseHelperText.NOT_FOUND.value,
                                        offerActualBillingPeriod = PurchaseHelperText.NOT_FOUND.value,
                                        offerBillingPeriod = PurchaseHelperText.NOT_FOUND.value,
                                        planOfferType = PlanOfferType.FREE_TRIAL.takeIf { freeTrialPeriod.lowercase() != PurchaseHelperText.NOT_FOUND.value.lowercase() } ?: PlanOfferType.BASE_PLAN,
                                        productDetail = productDetail
                                    )
                                )

                                logProductDetail(
                                    fMethodName = methodName,
                                    fProductDetail = productDetail
                                )
                            }
                        }
                        onComplete.invoke()
                    } ?: kotlin.run {
                        onComplete.invoke()
                    }
                } else {
                    logResponseCode(
                        responseMsg = "$methodName: ",
                        billingResult = productDetails.billingResult
                    )
                    onComplete.invoke()
                }
            } else {
                onComplete.invoke()
            }
        } ?: kotlin.run {
            onComplete.invoke()
        }
    }

    private fun onPurchased(context: Context, productType: String) {
        logE(tag = TAG, message = "onPurchased: Purchase Success")
        when (productType) {
            BillingClient.ProductType.INAPP -> {
                AdsManager(context).onProductPurchased()
            }

            BillingClient.ProductType.SUBS -> {
                AdsManager(context).onProductSubscribed()
            }

            "Test Purchase" -> {
                AdsManager(context).onProductTestPurchase()
            }
        }
    }

    private fun onExpired(context: Context, productType: String) {
        logE(tag = TAG, message = "onExpired: Purchase Expired")
        if (productType == BillingClient.ProductType.INAPP) {
            AdsManager(context).onProductExpired()
        } else if (productType == BillingClient.ProductType.SUBS) {
            AdsManager(context).onSubscribeExpired()
        }
    }
    //</editor-fold>

    //<editor-fold desc="Handle Purchase">
    private fun handlePurchase(context: Context, purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            purchase.products.forEach { product ->
                when {
                    lifeTimeProductKeyList.contains(product) -> {
                        logI(tag = TAG, message = "handlePurchase: Life-Time =>> productId -> $product")
                        CoroutineScope(Dispatchers.Main).launch {
                            AdsManager(context).onProductPurchased()
                        }
                    }

                    subscriptionKeyList.contains(product) -> {
                        logI(tag = TAG, message = "handlePurchase: Subscribe =>> productId -> $product")
                        CoroutineScope(Dispatchers.Main).launch {
                            AdsManager(context).onProductSubscribed()
                        }
                    }

                    else -> logI(tag = TAG, message = "handlePurchase: Other =>> productId -> $product")
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                mPurchaseListener?.onPurchasedSuccess()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            acknowledgePurchase(purchase = purchase)
            if (isConsumable) {
                consumePurchase(purchase = purchase)
            }
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            val ackPurchaseResult = withContext(Dispatchers.IO) {
                mBillingClient?.acknowledgePurchase(acknowledgePurchaseParams)
            }

            ackPurchaseResult?.let {
                logResponseCode("acknowledgePurchase: ", it)
            } ?: kotlin.run {
                logE(tag = TAG, message = "acknowledgePurchase: =>> Not Found Any Purchase Result")
            }
        }

        logPurchaseItem(purchase = purchase)
    }

    private suspend fun consumePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        mBillingClient?.let {
            val consumeResult = it.consumePurchase(consumeParams)
            logResponseCode("consumePurchase: ", consumeResult.billingResult)
        }
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Product & Subscribe">
    fun purchase(
        activity: Activity,
        productId: String,
        fIsConsumable: Boolean = false
    ) {
        val isLifeTimeKey: Boolean = lifeTimeProductKeyList.any { it.getSKU.contains(productId.getSKU) }
        val isSubscriptionKey: Boolean = subscriptionKeyList.any { it.getSKU.contains(productId.getSKU) }
        isConsumable = fIsConsumable

        logE(TAG, "purchase: isLifeTimeKey::$isLifeTimeKey, isSubscriptionKey::$isSubscriptionKey")

        CoroutineScope(Dispatchers.IO).launch {
            if (isLifeTimeKey || isSubscriptionKey) {
                purchaseSelectedProduct(
                    methodName = "purchaseProduct".takeIf { isLifeTimeKey } ?: "subscribeProduct",
                    activity = activity,
                    productId = productId.getSKU,
                    productKeyList = lifeTimeProductKeyList.takeIf { isLifeTimeKey } ?: subscriptionKeyList,
                    productType = BillingClient.ProductType.INAPP.takeIf { isLifeTimeKey } ?: BillingClient.ProductType.SUBS
                )
            }
        }
    }

    private suspend fun purchaseSelectedProduct(
        methodName: String,
        activity: Activity,
        productId: String,
        productKeyList: java.util.ArrayList<String>,
        productType: String
    ) {
        mBillingClient?.let { billingClient ->
            if (billingClient.isReady) {
                val purchaseParams = QueryProductDetailsParams.newBuilder()
                    .setProductList(
                        productKeyList.map { keyId ->
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(keyId.getSKU)
                                .setProductType(productType)
                                .build()
                        }
                    )
                    .build()

                val productDetailsResult = withContext(Dispatchers.IO) {
                    billingClient.queryProductDetails(purchaseParams)
                }

                productDetailsResult.productDetailsList?.getProductDetails(productId = productId)?.let { productDetail ->
                    val offerToken = productDetail.subscriptionOfferDetails?.get(0)?.offerToken.takeIf { productType == BillingClient.ProductType.SUBS }

                    if (offerToken == null && productType == BillingClient.ProductType.SUBS) {
                        return
                    }

                    val lBuilder: BillingFlowParams.ProductDetailsParams.Builder = BillingFlowParams.ProductDetailsParams.newBuilder().apply {
                        this.setProductDetails(productDetail)
                        offerToken?.let {
                            this.setOfferToken(it)
                        }
                    }

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(mutableListOf(lBuilder.build()))
                        .build()

                    val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

                    when (billingResult.responseCode) {
                        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                            logE(tag = TAG, message = "$methodName: =>> ITEM_ALREADY_OWNED")
                            onPurchased(context = activity, productType = productType)
                            CoroutineScope(Dispatchers.Main).launch {
                                mPurchaseListener?.onProductAlreadyOwn()
                            }
                            logProductDetail(
                                fMethodName = methodName,
                                fProductDetail = productDetail
                            )
                        }

                        BillingClient.BillingResponseCode.OK -> logE(tag = TAG, message = "$methodName: =>> Purchase in Progress")

                        else -> logResponseCode(responseMsg = methodName, billingResult = billingResult)
                    }


                } ?: kotlin.run {
                    logE(
                        tag = TAG,
                        message = "$methodName: =>> Product Detail not found for product id:: $productId"
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        mPurchaseListener?.onBillingKeyNotFound(productId)
                    }
                }
            } else {
                logE(TAG, "$methodName: =>> The billing client is not ready")
                CoroutineScope(Dispatchers.Main).launch {
                    activity.toasts(messageId = R.string.error_billing_client_not_ready)
                }
            }
        } ?: kotlin.run {
            logE(TAG, "$methodName: =>> The billing client is NULL")
            CoroutineScope(Dispatchers.Main).launch {
                activity.toasts(messageId = R.string.error_billing_client_null)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="other get function">
    private fun Context.toasts(@StringRes messageId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@toasts, this@toasts.getStringRes(messageId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun List<ProductDetails>.getProductDetails(productId: String): ProductDetails? = this.find { it.productId.equals(productId, true) }

    fun String.getFullBillingPeriod(context: Context): String {
        return try {
            if (this.equals("OTP", true)) {
                getLocalizedString<String>(
                    context = context,
                    resourceId = R.string.one_time_purchase,
                )
            } else {
                val size = this.length
                val periodCount: Int = this.substring(1, size - 1).toInt()
                when (this.substring(size - 1, size)) {
                    "D", "d" -> {
                        val periodString: String = getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.period_days.takeIf { periodCount > 1 } ?: R.string.period_day,
                        )
                        getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.str_1_str_2,
                            formatArgs = arrayOf("$periodCount", periodString)
                        )
                    }

                    "W", "w" -> {
                        if (periodCount == 1) {
                            val dayCount = 7
                            val periodString: String = getLocalizedString<String>(
                                context = context,
                                resourceId = R.string.period_days,
                            )
                            getLocalizedString<String>(
                                context = context,
                                resourceId = R.string.str_1_str_2,
                                formatArgs = arrayOf("$dayCount", periodString)
                            )
                        } else {
                            val periodString: String = getLocalizedString<String>(
                                context = context,
                                resourceId = R.string.period_weeks.takeIf { periodCount > 1 } ?: R.string.period_week,
                            )
                            getLocalizedString<String>(
                                context = context,
                                resourceId = R.string.str_1_str_2,
                                formatArgs = arrayOf("$periodCount", periodString)
                            )
                        }
                    }

                    "M", "m" -> {
                        val periodString: String = getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.period_months.takeIf { periodCount > 1 } ?: R.string.period_month,
                        )
                        getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.str_1_str_2,
                            formatArgs = arrayOf("$periodCount", periodString)
                        )
                    }

                    "Y", "y" -> {
                        val periodString: String = getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.period_years.takeIf { periodCount > 1 } ?: R.string.period_year,
                        )
                        getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.str_1_str_2,
                            formatArgs = arrayOf("$periodCount", periodString)
                        )
                    }

                    else -> PurchaseHelperText.NOT_FOUND.value
                }
            }
        } catch (e: Exception) {
            logE(TAG, "getFullBillingPeriod Exception: ${e.message}")
            PurchaseHelperText.NOT_FOUND.value
        }
    }

    fun String.getBillingPeriodCount(): Int {
        return try {
            if (this.equals("OTP", true)) {
                -1
            } else {
                val size = this.length
                val periodCount: Int = this.substring(1, size - 1).toInt()
                when (this.substring(size - 1, size)) {
                    "D", "d" -> periodCount

                    "W", "w" -> 7.takeIf { periodCount == 1 } ?: periodCount

                    "M", "m" -> periodCount

                    "Y", "y" -> periodCount

                    else -> -1
                }
            }
        } catch (e: Exception) {
            logE(TAG, "getBillingPeriodCount Exception: ${e.message}")
            -1
        }
    }

    fun String.getBillingPeriodName(context: Context): String {
        return try {
            if (this.equals("OTP", true)) {
                getLocalizedString<String>(
                    context = context,
                    resourceId = R.string.one_time_purchase,
                )
            } else {
                val size = this.length

                when (this.substring(size - 1, size)) {
                    "D", "d" -> {
                        getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.period_day,
                        )
                    }

                    "W", "w" -> {
                        getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.period_week,
                        )
                    }

                    "M", "m" -> {
                        getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.period_month,
                        )
                    }

                    "Y", "y" -> {
                        getLocalizedString<String>(
                            context = context,
                            resourceId = R.string.period_year,
                        )
                    }

                    else -> PurchaseHelperText.NOT_FOUND.value
                }
            }
        } catch (e: Exception) {
            logE(TAG, "getBillingPeriodName Exception: ${e.message}")
            PurchaseHelperText.NOT_FOUND.value
        }
    }
    //</editor-fold>

    //<editor-fold desc="Price Discount">
    private fun Double.getDiscountPercentage(basePrize: Double): Int {
        var lDiscountPercentage: Double = (basePrize / this) * 100
        lDiscountPercentage *= 100
        lDiscountPercentage = lDiscountPercentage.toInt().toDouble()
        lDiscountPercentage /= 100

        return lDiscountPercentage.roundToInt()
    }

    private fun String.getDiscountPrice(baseNumber: Double, newNumber: Double): String = this.replace(
        String.format(Locale.ENGLISH, "%.2f", baseNumber),
        String.format(Locale.ENGLISH, "%.2f", newNumber),
        false
    )

    fun getWeekBaseYearlyDiscount(
        weekPrice: String,
        yearPrice: String,
        onDiscountCalculated: (discountPercentage: Int, discountPrice: String) -> Unit
    ) {
        weekPrice.getPriceInDouble.let { lWeekNumber ->
            yearPrice.getPriceInDouble.let { lYearNumber ->
                val lWeekPrize: Double = (lWeekNumber * 52) - lYearNumber
                val lYearPrizeBaseOfWeek = (lWeekNumber * 52)

                val lDiscountPercentage = lYearPrizeBaseOfWeek.getDiscountPercentage(basePrize = lWeekPrize)
                val lDiscountPrice = weekPrice.getDiscountPrice(baseNumber = lWeekNumber, newNumber = (lYearNumber / 52))

                logE(TAG, "getWeekBaseYearlyDiscount: lDiscountPercentage::-> $lDiscountPercentage, lDiscountPrice::-> $lDiscountPrice")
                onDiscountCalculated.invoke(lDiscountPercentage, lDiscountPrice)
            }
        }
    }

    fun getWeekBaseMonthlyDiscount(
        weekPrice: String,
        monthPrice: String,
        onDiscountCalculated: (discountPercentage: Int, discountPrice: String) -> Unit
    ) {
        weekPrice.getPriceInDouble.let { lWeekNumber ->
            monthPrice.getPriceInDouble.let { lMonthNumber ->
                val lWeekPrize: Double = (lWeekNumber * 4) - lMonthNumber
                val lMonthPrizeBaseOfWeek = (lWeekNumber * 4)

                val lDiscountPercentage = lMonthPrizeBaseOfWeek.getDiscountPercentage(basePrize = lWeekPrize)
                val lDiscountPrice = weekPrice.getDiscountPrice(baseNumber = lWeekNumber, newNumber = (lMonthNumber / 4))

                logE(TAG, "getWeekBaseMonthlyDiscount: lDiscountPercentage::-> $lDiscountPercentage, lDiscountPrice::-> $lDiscountPrice")
                onDiscountCalculated.invoke(lDiscountPercentage, lDiscountPrice)
            }
        }
    }

    fun getMonthBaseYearlyDiscount(
        monthPrice: String,
        yearPrice: String,
        onDiscountCalculated: (discountPercentage: Int, discountPrice: String) -> Unit
    ) {
        monthPrice.getPriceInDouble.let { lMonthNumber ->
            yearPrice.getPriceInDouble.let { lYearNumber ->
                val lMonthPrize: Double = (lMonthNumber * 12) - lYearNumber
                val lYearPrizeBaseOfMonth = (lMonthNumber * 12)

                val lDiscountPercentage = lYearPrizeBaseOfMonth.getDiscountPercentage(basePrize = lMonthPrize)
                val lDiscountPrice = monthPrice.getDiscountPrice(baseNumber = lMonthNumber, newNumber = (lYearNumber / 12))

                logE(TAG, "getMonthBaseYearlyDiscount: lDiscountPercentage::-> $lDiscountPercentage, lDiscountPrice::-> $lDiscountPrice")
                onDiscountCalculated.invoke(lDiscountPercentage, lDiscountPrice)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="All Log Related Purchase">
    private fun logResponseCode(responseMsg: String, billingResult: BillingResult) {
        val errorCode = when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> "RESULT OK"
            BillingClient.BillingResponseCode.ERROR -> "ERROR"
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "FEATURE_NOT_SUPPORTED"
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
            BillingClient.BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
            else -> "unDefined Error"
        }
        logE(tag = TAG, message = "$responseMsg :: \nerrorCode::$errorCode,\nMessage::${billingResult.debugMessage}")
    }

    private fun logPurchaseItem(purchase: Purchase) {
        with(purchase) {
            logW(tag = TAG, message = "<<<-----------------   Purchase Details   ----------------->>>")
            logW(tag = TAG, message = "Order Id: $orderId")
            logW(tag = TAG, message = "Original Json: $originalJson")
            logW(tag = TAG, message = "Package Name: $packageName")
            logW(tag = TAG, message = "Purchase Token: $purchaseToken")
            logW(tag = TAG, message = "Signature: $signature")
            products.forEach {
                logW(tag = TAG, message = "Products: $it")
                logW(tag = TAG, message = "Price: ${it.getProductInfo?.formattedPrice}")
            }
            logW(tag = TAG, message = "Purchase State: ${purchaseState.getPurchaseState}")
            logW(tag = TAG, message = "Quantity: $quantity")
            logW(tag = TAG, message = "Purchase Time: $purchaseTime")
            logW(tag = TAG, message = "Acknowledged: $isAcknowledged")
            logW(tag = TAG, message = "AutoRenewing: $isAutoRenewing")
            logW(tag = TAG, message = "<<<-----------------   End of Purchase Details   ----------------->>>")
        }
    }

    private fun logProductDetail(fMethodName: String, fProductDetail: ProductDetails) {
        with(fProductDetail) {
            logW(tag = TAG, message = "\n")
            logW(tag = TAG, message = "$fMethodName: <<<-----------------   \"$productId\" Product Details   ----------------->>>")
            logW(tag = TAG, message = "$fMethodName: Product Id:: $productId")
            logW(tag = TAG, message = "$fMethodName: Name:: $name")
            logW(tag = TAG, message = "$fMethodName: Title:: $title")
            logW(tag = TAG, message = "$fMethodName: Description:: $description")
            logW(tag = TAG, message = "$fMethodName: Product Type:: $productType")
            oneTimePurchaseOfferDetails?.let { details ->
                with(details) {
                    logW(tag = TAG, message = "\n")
                    logW(tag = TAG, message = "$fMethodName: <<<-----------------   Life-Time Purchase Product Price Details   ----------------->>>")
                    logW(tag = TAG, message = "$fMethodName: Price Amount Micros:: $priceAmountMicros")
                    logW(tag = TAG, message = "$fMethodName: Formatted Price:: $formattedPrice")
                    logW(tag = TAG, message = "$fMethodName: Price Currency Code:: $priceCurrencyCode")
                    logW(tag = TAG, message = "$fMethodName: <<<-----------------   End of Life-Time Purchase Product Price Details   ----------------->>>")
                }
            }
            subscriptionOfferDetails?.let { details ->
                if (details.isNotEmpty()) {
                    details.forEachIndexed { index, subscriptionOfferDetails ->
                        subscriptionOfferDetails?.let { offerDetails ->
                            with(offerDetails) {
                                logW(tag = "", message = "\n")
                                logW(tag = TAG, message = "$fMethodName: <<<-----------------   Product Offer Details of Index:: $index   ----------------->>>")
                                logW(tag = TAG, message = "$fMethodName: Offer Token:: $offerToken")
                                logW(tag = TAG, message = "$fMethodName: Offer Tags:: $offerTags")

                                if (pricingPhases.pricingPhaseList.isNotEmpty()) {
                                    pricingPhases.pricingPhaseList.forEachIndexed { index, pricingPhase1 ->
                                        pricingPhase1?.let { pricingPhase ->
                                            with(pricingPhase) {
                                                logW(tag = "", message = "\n")
                                                logW(tag = TAG, message = "$fMethodName: <<<-----------------   Product Offer Price Details of Index:: $index   ----------------->>>")
                                                logW(tag = TAG, message = "$fMethodName: Billing Period:: $billingPeriod")
                                                logW(tag = TAG, message = "$fMethodName: Formatted Price:: $formattedPrice")
                                                logW(tag = TAG, message = "$fMethodName: Price Amount Micros:: $priceAmountMicros")
                                                logW(tag = TAG, message = "$fMethodName: Price Currency Code:: $priceCurrencyCode")
                                                logW(tag = TAG, message = "$fMethodName: Recurrence Mode:: $recurrenceMode")
                                                logW(tag = TAG, message = "$fMethodName: Billing Cycle Count:: $billingCycleCount")
                                                logW(tag = TAG, message = "$fMethodName: <<<-----------------   End of Product Offer Price Details of Index:: $index   ----------------->>>")
                                            }
                                        }
                                    }
                                }

                                logW(tag = TAG, message = "$fMethodName: <<<-----------------   End of Product Offer Details of Index:: $index   ----------------->>>")
                            }
                        }
                    }
                }
            }
            logW(tag = TAG, message = "$fMethodName: <<<-----------------   End of \"$productId\" Product Details   ----------------->>>")
        }
    }

    private fun logPurchaseHistory(fMethodName: String, purchaseHistoryRecordList: List<Purchase>) {
        if (isPurchaseHistoryLogEnable) {
            logW(tag = TAG, message = "\n")
            logW(tag = TAG, message = "$fMethodName: <<<-----------------   Purchase History Product Details   ----------------->>>")
            purchaseHistoryRecordList.forEachIndexed { index, purchaseHistoryRecord ->
                purchaseHistoryRecord.let {
                    logW(tag = "", message = "\n")
                    logW(tag = TAG, message = "$fMethodName: <<<-----------------   Product History Details of Index:: $index   ----------------->>>")
                    logW(tag = TAG, message = "$fMethodName: purchaseToken:: ${it.purchaseToken}")
                    logW(tag = TAG, message = "$fMethodName: purchaseTime:: ${it.purchaseTime}")
                    logW(tag = TAG, message = "$fMethodName: products:: ${it.products}")
                    logW(tag = TAG, message = "$fMethodName: quantity:: ${it.quantity}")
                    logW(tag = TAG, message = "$fMethodName: signature:: ${it.signature}")
                    logW(tag = TAG, message = "$fMethodName: developerPayload:: ${it.developerPayload}")
                    logW(tag = TAG, message = "$fMethodName: originalJson:: ${it.originalJson}")
                    logW(tag = TAG, message = "$fMethodName: <<<-----------------   End of Product History Details of Index:: $index   ----------------->>>")
                }
            }
            logW(tag = TAG, message = "$fMethodName: <<<-----------------   End of Purchase History Product Details   ----------------->>>")
        }
    }
    //</editor-fold>

    fun setPurchaseListener(listener: ProductPurchaseListener) {
        mPurchaseListener = listener
    }

    fun fireTestingPurchase(context: Context) {
        onPurchased(context = context, productType = "Test Purchase")
        mPurchaseListener?.onPurchasedSuccess()
    }

    interface ProductPurchaseListener {
        //        fun onPurchasedSuccess(purchase: Purchase) {}
        fun onPurchasedSuccess() {}
        fun onProductAlreadyOwn() {}
        fun onBillingSetupFinished()
        fun onBillingKeyNotFound(productId: String) {}
    }

//    val isNeedToLaunchTimeLineScreen: Boolean
//        get() {
//            val isFreeTrialPeriod: Boolean = (PRODUCT_LIST.any { it.planOfferType == PlanOfferType.FREE_TRIAL })
//            return isFreeTrialPeriod
//        }
fun isNeedToLaunchTimeLineScreen(timeLinePlanType: TimeLinePlanType): Boolean {
    val isFreeTrialPeriod = (PRODUCT_LIST.firstOrNull() { it.planOfferType == PlanOfferType.FREE_TRIAL })
    Log.e(TAG, "isFreeTrialPeriod: $isFreeTrialPeriod", )
    when (timeLinePlanType) {
        TimeLinePlanType.LIFETIME -> {
            return getLifeTimeProductInfo?.planOfferType == PlanOfferType.FREE_TRIAL
        }

        TimeLinePlanType.YEARLY -> {
            return getYearlyProductInfo?.planOfferType == PlanOfferType.FREE_TRIAL
        }

        TimeLinePlanType.MONTHLY -> {
            return getMonthlyProductInfo?.planOfferType == PlanOfferType.FREE_TRIAL
        }

        TimeLinePlanType.WEEKLY -> {
            return getWeeklyProductInfo?.planOfferType == PlanOfferType.FREE_TRIAL
        }
    }
}
    private val String.isDiscountedSKU: Boolean get() = this.lowercase().contains("DISCOUNT".lowercase(), true) or this.lowercase().contains("disc".lowercase(), true)

    val String.isMonthlySKU: Boolean get() = this.lowercase().contains("MONTH".lowercase(), true) && (!(this.isDiscountedSKU))
    val String.isWeeklySKU: Boolean get() = this.lowercase().contains("WEEK".lowercase(), true) && (!(this.isDiscountedSKU))
    val String.isYearlySKU: Boolean get() = this.lowercase().contains("YEAR".lowercase(), true) && (!(this.isDiscountedSKU))
    val String.isLifeTimeSKU: Boolean get() = (this.lowercase().contains("LIFETIME".lowercase(), true) or this.lowercase().contains("ONETIME".lowercase(), true)) && (!(this.isDiscountedSKU))

    private val listOfFreeTrailPlanPriority: ArrayList<String> = arrayListOf(
        "MONTH",
        "WEEK",
        "YEAR",
        "LIFETIME",
        "ONETIME",
    )

    val getFreeTrialProductInfo: ProductInfo?
        get() {
            var freeTrialPlanInfo: ProductInfo? = null

            val freeTrialList = PRODUCT_LIST.filter { it.planOfferType == PlanOfferType.FREE_TRIAL }.sortedWith(compareBy { item ->
                listOfFreeTrailPlanPriority.indexOfFirst { keyword -> item.id.getSKU.contains(keyword, ignoreCase = true) }
            }).toMutableList()

            if (freeTrialList.isNotEmpty()) {
                freeTrialPlanInfo = freeTrialList[0]
                logE(TAG, "getFreeTrialProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
                logE(TAG, "getFreeTrialProductInfo: $freeTrialPlanInfo")
                logE(TAG, "getFreeTrialProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
            }

            return freeTrialPlanInfo
        }

    val getLifeTimeProductInfo: ProductInfo?
        get() {
            var lifeTimePlanInfo: ProductInfo? = null

            val lifeTimeList = PRODUCT_LIST.filter { lifeTimeProductKeyList.contains(it.id.getSKU) }.sortedWith(compareBy { item ->
                listOfFreeTrailPlanPriority.indexOfFirst { keyword -> item.id.getSKU.contains(keyword, ignoreCase = true) }
            }).toMutableList()

            lifeTimeList.forEachIndexed { index, productInfo ->
                logE(TAG, "LifeTimeProductInfo: index::-> $index, \n$productInfo")
            }

            if (lifeTimeList.isNotEmpty()) {
                lifeTimePlanInfo = lifeTimeList[0]
                logE(TAG, "getLifeTimeProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
                logE(TAG, "getLifeTimeProductInfo: $lifeTimePlanInfo")
                logE(TAG, "getLifeTimeProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
            }

            return lifeTimePlanInfo
        }

    val getMonthlyProductInfo: ProductInfo?
        get() {
            var monthlyPlanInfo: ProductInfo? = null

            val monthlyList = PRODUCT_LIST.filter { listOfProducts ->
                subscriptionKeyList.any { it.getSKU.contains(listOfProducts.id.getSKU) }
                        && listOfProducts.id.getSKU.isMonthlySKU
            }.sortedWith(compareBy { item ->
                listOfFreeTrailPlanPriority.indexOfFirst { keyword ->
                    item.id.getSKU.contains(keyword, ignoreCase = true)
                }
            }).toMutableList()

            monthlyList.forEachIndexed { index, productInfo ->
                logE(TAG, "MonthlyProductInfo: index::-> $index, \n$productInfo")
            }

            if (monthlyList.isNotEmpty()) {
                monthlyPlanInfo = monthlyList[0]
                logE(TAG, "getMonthlyProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
                logE(TAG, "getMonthlyProductInfo: $monthlyPlanInfo")
                logE(TAG, "getMonthlyProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
            }

            return monthlyPlanInfo
        }

    val getWeeklyProductInfo: ProductInfo?
        get() {
            var weeklyPlanInfo: ProductInfo? = null

            val weeklyList = PRODUCT_LIST.filter { listOfProducts ->
                subscriptionKeyList.any { it.getSKU.contains(listOfProducts.id.getSKU) }
                        && listOfProducts.id.getSKU.isWeeklySKU
            }.sortedWith(compareBy { item ->
                listOfFreeTrailPlanPriority.indexOfFirst { keyword ->
                    item.id.getSKU.contains(keyword, ignoreCase = true)
                }
            }).toMutableList()

            if (weeklyList.isNotEmpty()) {
                weeklyPlanInfo = weeklyList[0]
                logE(TAG, "getWeeklyProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
                logE(TAG, "getWeeklyProductInfo: $weeklyPlanInfo")
                logE(TAG, "getWeeklyProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
            }

            return weeklyPlanInfo
        }

    val getYearlyProductInfo: ProductInfo?
        get() {
            var yearlyPlanInfo: ProductInfo? = null

            val yearlyList = PRODUCT_LIST.filter { listOfProducts ->
                subscriptionKeyList.any { it.getSKU.contains(listOfProducts.id.getSKU) }
                        && listOfProducts.id.getSKU.isYearlySKU
            }.sortedWith(compareBy { item ->
                listOfFreeTrailPlanPriority.indexOfFirst { keyword ->
                    item.id.getSKU.contains(keyword, ignoreCase = true)
                }
            }).toMutableList()

            yearlyList.forEachIndexed { index, productInfo ->
                logE(TAG, "YearlyProductInfo: index::-> $index, \n$productInfo")
            }

            if (yearlyList.isNotEmpty()) {
                yearlyPlanInfo = yearlyList[0]
                logE(TAG, "getYearlyProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
                logE(TAG, "getYearlyProductInfo: $yearlyPlanInfo")
                logE(TAG, "getYearlyProductInfo: --*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--")
            }

            return yearlyPlanInfo
        }
}