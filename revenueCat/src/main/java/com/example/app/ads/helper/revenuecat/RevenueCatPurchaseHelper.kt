package com.example.app.ads.helper.revenuecat

import android.content.Context
import com.example.app.ads.helper.logE
import com.example.app.ads.helper.logI
import com.example.app.ads.helper.purchase.PlanOfferType
import com.example.app.ads.helper.purchase.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.ProductPurchaseHelper.getCurrencySymbol
import com.example.app.ads.helper.purchase.ProductPurchaseHelper.getFullBillingPeriod
import com.example.app.ads.helper.purchase.ProductPurchaseHelper.getSKU
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


fun initRevenueCat(fContext: Context, revenueCatID: String) {
    if (revenueCatID.isNotEmpty()) {
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(fContext, revenueCatID)
                .purchasesAreCompletedBy(PurchasesAreCompletedBy.REVENUECAT)
                .build()
        )
    }
}

fun initRevenueCatProductList(fContext: Context, onInitializationComplete: () -> Unit) {
    Purchases.sharedInstance.getOfferingsWith(
        onError = { error ->
            logE(tag = TAG, message = "initRevenueCatProductList: onError: $error")
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
                                logStringBuilder.append("\n")

                                ProductPurchaseHelper.PRODUCT_LIST.find { it.id.getSKU == products.product.purchasingData.productId.getSKU }?.let { foundProductInfo ->
                                    logStringBuilder.append("\nData From Google Billing ==> $foundProductInfo")
                                    logStringBuilder.append("\n")
                                    foundProductInfo.formattedPrice = product.price.formatted
                                    foundProductInfo.priceAmountMicros = product.price.amountMicros
                                    foundProductInfo.priceCurrencyCode = product.price.currencyCode
                                    foundProductInfo.priceCurrencySymbol = product.price.formatted.getCurrencySymbol
                                    foundProductInfo.actualBillingPeriod = product.period?.iso8601 ?: "OTP"
                                    foundProductInfo.billingPeriod = (product.period?.iso8601 ?: "OTP").getFullBillingPeriod(context = fContext)
                                    foundProductInfo.planOfferType = PlanOfferType.BASE_PLAN

                                    if (product.type == ProductType.SUBS) {
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