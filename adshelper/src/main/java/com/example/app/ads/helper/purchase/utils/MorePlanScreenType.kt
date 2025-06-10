package com.example.app.ads.helper.purchase.utils

/**
 * @author Akshay Harsoda
 * @since 20 Aug 2024
 */
enum class MorePlanScreenType(var value: String) {
    SIX_BOX_SCREEN("six_box_screen"),
    WEEKLY_SCREEN("weekly_screen"),
    FOUR_PLAN_SCREEN("four_plan_screen");

    companion object {

        @JvmStatic
        fun fromName(value: String): MorePlanScreenType {
            return MorePlanScreenType.entries.firstOrNull { it.value.lowercase() == value.lowercase() } ?: SIX_BOX_SCREEN
        }
    }
}