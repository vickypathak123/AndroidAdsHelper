package com.example.app.ads.helper

import android.app.Activity
import com.example.app.ads.helper.interstitialad.InterstitialAdHelper.showInterstitialAd
import com.example.app.ads.helper.openad.AppOpenAdHelper.showAppOpenAd
import com.example.app.ads.helper.purchase.product.AdsManager

object VasuSplashConfig {

    private val TAG: String = javaClass.simpleName

    /**
     * Manages the subscription flow and ad displays based on the provided index.
     *
     * - `Index == 1` : Opens the Subscription Screen. The developer will manage the next action after opening the Subscription Screen.
     * - `Index == 2` : Shows the App Open Ad. When it closes, the next action is performed.
     * - `Index == 3` : Shows the Interstitial Ad. When it closes, the next action is performed.
     * - `Index == else` : Performs the next action without showing ads or the Subscription Screen.
     *
     * @param fActivity Reference to the current Activity.
     * @param listOfInitialSubscriptionOpenFlow A list obtained from the remote config key "initial_subscription_open_flow".
     *                                           This list determines whether to show ads or the subscription screen before performing the next action.
     * @param onOpenSubscriptionScreen Callback for opening the subscription screen and performing the next action.
     * @param onNextAction Callback for performing the next action directly.
     */
    fun showSplashFlow(
        fActivity: Activity,
        vararg listOfInitialSubscriptionOpenFlow: Int,
        onOpenSubscriptionScreen: () -> Unit,
        onNextAction: () -> Unit,
    ) {
        val oldIndex: Int = AdsManager(context = fActivity).initialSubscriptionOpenFlowIndex

        val showIndex = if (oldIndex >= 0 && oldIndex < listOfInitialSubscriptionOpenFlow.size) {
            oldIndex
        } else {
            0
        }

        logE(TAG, "showSplashFlow: showIndex::-> ${listOfInitialSubscriptionOpenFlow[showIndex]}")

        val onAction: (isOpenSubscriptionScreen: Boolean) -> Unit = { isOpenSubscriptionScreen ->
            AdsManager(context = fActivity).initialSubscriptionOpenFlowIndex = showIndex + 1

            if (isOpenSubscriptionScreen) {
                onOpenSubscriptionScreen.invoke()
            } else {
                onNextAction.invoke()
            }
        }

        when (listOfInitialSubscriptionOpenFlow[showIndex]) {
            1 -> onAction.invoke(true)

            2 -> {
                fActivity.showAppOpenAd {
                    onAction.invoke(false)
                }
            }

            3 -> {
                fActivity.showInterstitialAd { _, _ ->
                    onAction.invoke(false)
                }
            }

            else -> onAction.invoke(false)
        }
    }
}