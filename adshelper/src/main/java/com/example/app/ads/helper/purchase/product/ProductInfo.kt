package com.example.app.ads.helper.purchase.product

import android.os.Parcelable
import androidx.annotation.Keep
import com.android.billingclient.api.ProductDetails
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @author Akshay Harsoda
 * @since 21 Jul 2021
 * @updated 22 Jul 2024
 */

@Keep
@Parcelize
open class ProductInfo(
    @Expose
    @SerializedName("product_id")
    open val id: String = "",
    @Expose
    @SerializedName("formatted_price")
    open var formattedPrice: String,
    @Expose
    @SerializedName("price_amount_micros")
    open var priceAmountMicros: Long,
    @Expose
    @SerializedName("price_currency_code")
    open var priceCurrencyCode: String,
    @Expose
    @SerializedName("price_currency_symbol")
    open var priceCurrencySymbol: String,
    @Expose
    @SerializedName("actual_billing_period")
    open var actualBillingPeriod: String,
    @Expose
    @SerializedName("billing_period")
    open var billingPeriod: String,

    @Expose
    @SerializedName("actual_free_trial_period")
    open var actualFreeTrialPeriod: String,
    @Expose
    @SerializedName("free_trial_period")
    open var freeTrialPeriod: String,


    @Expose
    @SerializedName("offer_formatted_price")
    open var offerFormattedPrice: String,
    @Expose
    @SerializedName("offer_price_amount_micros")
    open var offerPriceAmountMicros: Long,
    @Expose
    @SerializedName("offer_price_currency_code")
    open var offerPriceCurrencyCode: String,
    @Expose
    @SerializedName("offer_price_currency_symbol")
    open var offerPriceCurrencySymbol: String,
    @Expose
    @SerializedName("offer_actual_billing_period")
    open var offerActualBillingPeriod: String,
    @Expose
    @SerializedName("offer_billing_period")
    open var offerBillingPeriod: String,


    @Expose
    @SerializedName("plan_offer_type")
    open var planOfferType: PlanOfferType,


    @Expose
    @SerializedName("product_detail")
    open val productDetail: @RawValue ProductDetails? = null,
) : Parcelable {
    override fun toString(): String {
        return StringBuilder().apply {
            append("ProductInfo{")
            append("id='${id}', ")
            append("formattedPrice='${formattedPrice}', ")
            append("priceAmountMicros='${priceAmountMicros}', ")
            append("priceCurrencyCode='${priceCurrencyCode}', ")
            append("priceCurrencySymbol='${priceCurrencySymbol}', ")
            append("actualBillingPeriod='${actualBillingPeriod}', ")
            append("billingPeriod='${billingPeriod}', ")
            append("actualFreeTrialPeriod='${actualFreeTrialPeriod}', ")
            append("freeTrialPeriod='${freeTrialPeriod}', ")
            append("offerFormattedPrice='${offerFormattedPrice}', ")
            append("offerPriceAmountMicros='${offerPriceAmountMicros}', ")
            append("offerPriceCurrencyCode='${offerPriceCurrencyCode}', ")
            append("offerPriceCurrencySymbol='${offerPriceCurrencySymbol}', ")
            append("offerActualBillingPeriod='${offerActualBillingPeriod}', ")
            append("offerBillingPeriod='${offerBillingPeriod}', ")
            append("planOfferType='${planOfferType}', ")
            append("productDetail='${productDetail}'")
            append("}")
        }.toString()
    }
}

enum class PlanOfferType {
    FREE_TRIAL,
    INTRO_OFFER,
    BASE_PLAN,
}
