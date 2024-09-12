package com.example.app.ads.helper.revenuecat

import android.content.Context
import com.example.app.ads.helper.isInternetAvailable
import com.example.app.ads.helper.isOnline
import com.example.app.ads.helper.purchase.getCurrencySymbol
import com.example.app.ads.helper.purchase.product.PlanOfferType
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.checkIsAllPriceLoaded
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getFullBillingPeriod
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getSKU
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesAreCompletedBy
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.getOfferingsWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


private const val TAG: String = "Akshay_Admob_RevenueCatPurchaseHelper"

private fun setBillingListener(fContext: Context): Job {
    val job: Job = CoroutineScope(Dispatchers.IO).launch {
        logE(TAG, "setBillingListener: Set Listener")
        ProductPurchaseHelper.setPurchaseListener(object : ProductPurchaseHelper.ProductPurchaseListener {
            override fun onBillingSetupFinished() {
                initRevenueCatProductList(fContext = fContext, onInitializationComplete = {})
            }
        })
    }
    return job
}

fun initRevenueCat(fContext: Context, revenueCatID: String) {
    if (revenueCatID.isNotEmpty()) {

        var isOfflineCall: Boolean = !isOnline

        if (isOfflineCall) {
            isInternetAvailable.observeForever {
                if (it) {
                    if (isOfflineCall) {
                        isOfflineCall = false
                        if (!checkIsAllPriceLoaded) {
                            val job: Job = setBillingListener(fContext = fContext)
                            runBlocking {
                                job.join()
                                CoroutineScope(Dispatchers.IO).launch {
                                    logE(TAG, "initView: InitBilling")
                                    ProductPurchaseHelper.initBillingClient(fContext = fContext)
                                }
                            }
                        }
                    }
                }
            }
        }


        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(fContext, revenueCatID)
                .purchasesAreCompletedBy(PurchasesAreCompletedBy.REVENUECAT)
                .build()
        )

        /*Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
            val logStringBuilder: StringBuilder = StringBuilder().apply {
                this.append("\n\n initRevenueCat:\n")
            }

            logStringBuilder.append("\n <<<-----------------   START CUSTOMER INFO   ----------------->>>")

            logStringBuilder.append("\n entitlements.all::-> ${customerInfo.entitlements.all.values}")
            logStringBuilder.append("\n entitlements.verification::-> ${customerInfo.entitlements.verification}")
            logStringBuilder.append("\n allExpirationDatesByProduct::-> ${customerInfo.allExpirationDatesByProduct}")
            logStringBuilder.append("\n allPurchaseDatesByProduct::-> ${customerInfo.allPurchaseDatesByProduct}")
            logStringBuilder.append("\n requestDate::-> ${customerInfo.requestDate}")
            logStringBuilder.append("\n schemaVersion::-> ${customerInfo.schemaVersion}")
            logStringBuilder.append("\n firstSeen::-> ${customerInfo.firstSeen}")
            logStringBuilder.append("\n originalAppUserId::-> ${customerInfo.originalAppUserId}")
            logStringBuilder.append("\n managementURL::-> ${customerInfo.managementURL}")
            logStringBuilder.append("\n originalPurchaseDate::-> ${customerInfo.originalPurchaseDate}")
            logStringBuilder.append("\n activeSubscriptions::-> ${customerInfo.activeSubscriptions}")
            logStringBuilder.append("\n allPurchasedProductIds::-> ${customerInfo.allPurchasedProductIds}")
            logStringBuilder.append("\n latestExpirationDate::-> ${customerInfo.latestExpirationDate}")
            logStringBuilder.append("\n nonSubscriptionTransactions::-> ${customerInfo.nonSubscriptionTransactions}")
            logStringBuilder.append("\n")

            logStringBuilder.append("\n <<<-----------------   END CUSTOMER INFO   ----------------->>>")

            logI(tag = TAG, message = logStringBuilder.toString())
        }*/
    }
}

/**
 * initialization of Product SKU data using RevenueCat Billing.
 *
 * @param fContext it's refers to your application, activity or fragment context.
 * @param onInitializationComplete Callback for data will be initialized.
 */
fun initRevenueCatProductList(fContext: Context, onInitializationComplete: () -> Unit) {
    Purchases.sharedInstance.getOfferingsWith(
        onError = { error ->
            logE(tag = TAG, message = "initRevenueCatProductList: onError: $error")
            onInitializationComplete.invoke()
        },
        onSuccess = { offerings ->
            offerings.current?.availablePackages?.let { listOfProducts ->
                listOfProducts.forEach { product ->
                    logE(tag = TAG, message = "initRevenueCatProductList: ${product.packageType} ::-> ${product.product.purchasingData.productId}")
                    if (product.packageType == PackageType.LIFETIME) {
                        ProductPurchaseHelper.addOtherLifeTimeProductKey(product.product.purchasingData.productId)
                    } else {
                        ProductPurchaseHelper.addOtherSubscriptionKey(product.product.purchasingData.productId)
                    }
                }

                ProductPurchaseHelper.initProductsKeys(fContext = fContext) {
                    val job: Job = CoroutineScope(Dispatchers.IO).launch {
                        listOfProducts.forEachIndexed { index, products ->
                            val logStringBuilder: StringBuilder = StringBuilder().apply {
                                this.append("\n\ninitRevenueCatProductList:\n")
                            }

                            products.product.let { product ->
                                logStringBuilder.append("\n <<<-----------------   START INDEX ::${index}   ----------------->>>")

                                logStringBuilder.append("\nid::-> ${product.id}")
                                logStringBuilder.append("\ntype::-> ${product.type}")
                                logStringBuilder.append("\npurchasingData::-> ${product.purchasingData}")
                                logStringBuilder.append("\nprice::-> ${product.price}")
                                logStringBuilder.append("\ndescription::-> ${product.description}")
                                logStringBuilder.append("\nname::-> ${product.name}")
                                logStringBuilder.append("\nperiod::-> ${product.period}")
                                logStringBuilder.append("\ntitle::-> ${product.title}")
                                logStringBuilder.append("\npresentedOfferingContext::-> ${product.presentedOfferingContext}")
                                logStringBuilder.append("\ndefaultOption::-> ${product.defaultOption}")
                                logStringBuilder.append("\nsubscriptionOptions::-> ${product.subscriptionOptions}")
                                logStringBuilder.append("\nfreeTrial::-> ${product.subscriptionOptions?.freeTrial?.billingPeriod}")
                                logStringBuilder.append("\n")
                                logStringBuilder.append("\n")
                                logStringBuilder.append("\n")

                                ProductPurchaseHelper.PRODUCT_LIST.find { it.id.getSKU == products.product.purchasingData.productId.getSKU }?.let { foundProductInfo ->
                                    logStringBuilder.append("\nData From Google Billing ==> $foundProductInfo")
                                    logStringBuilder.append("\n")
                                    logStringBuilder.append("\n")
                                    logStringBuilder.append("\n")
                                    foundProductInfo.formattedPrice = product.price.formatted
                                    foundProductInfo.priceAmountMicros = product.price.amountMicros
                                    foundProductInfo.priceCurrencyCode = product.price.currencyCode
                                    foundProductInfo.priceCurrencySymbol = product.price.formatted.getCurrencySymbol
                                    foundProductInfo.actualBillingPeriod = product.period?.iso8601 ?: "OTP"
                                    foundProductInfo.billingPeriod = (product.period?.iso8601 ?: "OTP").getFullBillingPeriod(context = fContext)
                                    foundProductInfo.planOfferType = PlanOfferType.BASE_PLAN

                                    if (product.type == ProductType.SUBS) {
                                        logStringBuilder.append("\nbasePlan ==> ${products.product.subscriptionOptions?.basePlan}")
                                        logStringBuilder.append("\nfreeTrial ==> ${products.product.subscriptionOptions?.freeTrial}")
                                        logStringBuilder.append("\nintroOffer ==> ${products.product.subscriptionOptions?.introOffer}")
                                        logStringBuilder.append("\n")
                                        logStringBuilder.append("\n")
                                        logStringBuilder.append("\n")

                                        products.product.subscriptionOptions?.defaultOffer?.let { defaultOffer ->
                                            logStringBuilder.append("\nfullPricePhase::-> ${defaultOffer.fullPricePhase}")
                                            logStringBuilder.append("\nfreePhase::-> ${defaultOffer.freePhase}")
                                            logStringBuilder.append("\nintroPhase::-> ${defaultOffer.introPhase}")
                                            logStringBuilder.append("\n")

                                            defaultOffer.fullPricePhase?.let { fullPricePhase ->
                                                foundProductInfo.formattedPrice = fullPricePhase.price.formatted
                                                foundProductInfo.priceAmountMicros = fullPricePhase.price.amountMicros
                                                foundProductInfo.priceCurrencyCode = fullPricePhase.price.currencyCode
                                                foundProductInfo.priceCurrencySymbol = fullPricePhase.price.formatted.getCurrencySymbol
                                                foundProductInfo.actualBillingPeriod = fullPricePhase.billingPeriod.iso8601
                                                foundProductInfo.billingPeriod = fullPricePhase.billingPeriod.iso8601.getFullBillingPeriod(context = fContext)
                                                foundProductInfo.planOfferType = PlanOfferType.BASE_PLAN
                                            }

                                            defaultOffer.freePhase?.let { freePhase ->
                                                foundProductInfo.actualFreeTrialPeriod = freePhase.billingPeriod.iso8601
                                                foundProductInfo.freeTrialPeriod = freePhase.billingPeriod.iso8601.getFullBillingPeriod(context = fContext)
                                                foundProductInfo.planOfferType = PlanOfferType.FREE_TRIAL
                                            } ?: defaultOffer.introPhase?.let { introPhase ->
                                                foundProductInfo.offerFormattedPrice = introPhase.price.formatted
                                                foundProductInfo.offerPriceAmountMicros = introPhase.price.amountMicros
                                                foundProductInfo.offerPriceCurrencyCode = introPhase.price.currencyCode
                                                foundProductInfo.offerPriceCurrencySymbol = introPhase.price.formatted.getCurrencySymbol
                                                foundProductInfo.offerActualBillingPeriod = introPhase.billingPeriod.iso8601
                                                foundProductInfo.offerBillingPeriod = introPhase.billingPeriod.iso8601.getFullBillingPeriod(context = fContext)
                                                foundProductInfo.planOfferType = PlanOfferType.INTRO_OFFER
                                            }
                                        }
                                    }

                                    logStringBuilder.append("\nData From RevenueCat Billing ==> $foundProductInfo")
                                    logStringBuilder.append("\n <<<-----------------   END INDEX ::${index}   ----------------->>>")
                                }

                                logI(tag = TAG, message = logStringBuilder.toString())


                            }
                        }
                    }
                    runBlocking {
                        job.join()
                        CoroutineScope(Dispatchers.Main).launch {
                            logE(tag = TAG, message = "initRevenueCatProductList: SUCCESS")
                            onInitializationComplete.invoke()
                        }
                    }
                }
            }
        }
    )
}


//fun getCurrencySymbol(currencyCode: String): String {
//    val currency = Currency.getInstance(currencyCode)
//    return currency.symbol
//}
//
//fun main() {
//    val currencyCode = "INR"
//    val currencySymbol = getCurrencySymbol(currencyCode)
//    println(currencySymbol) // Output: ₹
//}