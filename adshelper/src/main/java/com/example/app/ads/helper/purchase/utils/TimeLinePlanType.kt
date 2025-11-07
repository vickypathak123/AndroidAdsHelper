package com.example.app.ads.helper.purchase.utils

/**
 * @author Akshay Harsoda
 * @since 20 Aug 2024
 */
enum class TimeLinePlanType(var value: Int) {
    LIFETIME(0),
    YEARLY(1),
    MONTHLY(2),
    WEEKLY(3);

    companion object {

        @JvmStatic
        fun fromName(value: Int): TimeLinePlanType {
            return entries.firstOrNull { it.value == value } ?: YEARLY
        }
    }
}