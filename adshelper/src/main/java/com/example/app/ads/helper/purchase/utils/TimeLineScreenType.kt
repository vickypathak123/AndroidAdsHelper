package com.example.app.ads.helper.purchase.utils

/**
 * @author Akshay Harsoda
 * @since 20 Aug 2024
 */
enum class TimeLineScreenType(var value: String) {
    TIMELINE_SCREEN("timeline"),
    WEEKLY_SCREEN("weekly_screen");

    companion object {

        @JvmStatic
        fun fromName(value: String): TimeLineScreenType {
            return TimeLineScreenType.entries.firstOrNull { it.value.lowercase() == value.lowercase() } ?: TIMELINE_SCREEN
        }
    }
}