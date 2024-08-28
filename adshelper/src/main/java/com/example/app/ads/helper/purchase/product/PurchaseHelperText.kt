@file:Suppress("unused")

package com.example.app.ads.helper.purchase.product

/**
 * @author Akshay Harsoda
 * @since 22 Jul 2024
 */

enum class PurchaseHelperText(var value: String) {
    UNKNOWN("UNKNOWN"),
    PENDING("PENDING"),
    PURCHASED("PURCHASED"),
    UNSPECIFIED_STATE("UNSPECIFIED_STATE"),
    NOT_FOUND("NOT_FOUND");

    companion object {

        @JvmStatic
        fun fromName(value: String): PurchaseHelperText {
            return entries.firstOrNull { it.value.lowercase() == value.lowercase() } ?: UNKNOWN
        }
    }
}