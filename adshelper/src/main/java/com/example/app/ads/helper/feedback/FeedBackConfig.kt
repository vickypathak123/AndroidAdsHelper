@file:Suppress("unused")

package com.example.app.ads.helper.feedback

import android.app.Activity
import android.view.Gravity
import androidx.annotation.LayoutRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.purchase.VasuSubscriptionConfig.FourPlanScreenData
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItem
import com.example.app.ads.helper.purchase.sixbox.utils.TextGravityFlags
import java.io.Serializable
import java.lang.ref.WeakReference

object FeedBackConfig {

    /**
     * initialization of subscription screen ui data
     */
    @JvmStatic
    fun with(fActivity: Activity, fAppVersionName: String): ActivityData {
        return ActivityData(fActivity = fActivity, fAppVersionName = fAppVersionName)
    }

    class ActivityData(private val fActivity: Activity, private val fAppVersionName: String) : Serializable {

        @Suppress("PropertyName")
        val TAG: String = "FeedBackConfig_${javaClass.simpleName}"

        private val mContextRef: WeakReference<Activity> = WeakReference(fActivity)
        private val mActivity: Activity get() = mContextRef.get() ?: fActivity

        private var mLanguageCode: String = "en"

        private var mFeedBackScreenData: FeedBackScreenData = FeedBackScreenData(fActivity = fActivity)


        /**
         * @param fCode it's refers to your app language code.
         */
        @JvmName("setAppLanguageCode")
        fun setAppLanguageCode(fCode: String) = this@ActivityData.apply {
            this.mLanguageCode = fCode
        }

        @JvmName("setFeedBackScreenData")
        fun setFeedBackScreenData(fFeedBackScreenData: FeedBackScreenData) = this@ActivityData.apply {
            this.mFeedBackScreenData = fFeedBackScreenData
        }

        @JvmName("setFeedBackScreenData")
        fun setFeedBackScreenData(action: (fFeedBackScreenData: FeedBackScreenData) -> Unit) = this@ActivityData.apply {
            action.invoke(this.mFeedBackScreenData)
        }


        /**
         * launch subscription screen
         *
         * @param onScreenFinish callback for screen finish. [@param isUserPurchaseAnyPlan true if user will purchase any plan]
         */
        fun launchScreen(
            onScreenFinish: () -> Unit,
        ) {
            FeedBackActivity.launchScreen(
                fActivity = mActivity,
                screenData = Triple(mLanguageCode, mActivity.packageName, fAppVersionName),
                screenDataModel = FeedBackScreenDataModel(
                    backIconViewResource = mFeedBackScreenData.backIconViewResource,
                    screenTitleTextGravity = mFeedBackScreenData.screenTitleTextGravity,
                ),
                onScreenFinish = {
                    onScreenFinish.invoke()
                }
            )
        }

        class FeedBackScreenData(private val fActivity: Activity) : Serializable {

            @LayoutRes
            private var _backIconViewResource: Int = R.layout.item_toolbar_feedback

            @get:LayoutRes
            internal val backIconViewResource: Int get() = _backIconViewResource

            @TextGravityFlags
            private var _screenTitleTextGravity: Int = Gravity.CENTER

            @TextGravityFlags
            internal val screenTitleTextGravity: Int get() = _screenTitleTextGravity

            @JvmName("changeBackIcon")
            fun changeBackIcon(@LayoutRes resourceId: Int) = this@FeedBackScreenData.apply {
                this._backIconViewResource = resourceId
                return this
            }

            @JvmName("screenTitleTextGravity")
            fun screenTitleTextGravity(@TextGravityFlags gravity: Int) = this@FeedBackScreenData.apply {
                this._screenTitleTextGravity = gravity
                return this
            }
        }
    }
}