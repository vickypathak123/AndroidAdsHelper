package com.example.app.ads.helper.feedback

import android.view.Gravity
import androidx.annotation.LayoutRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.purchase.sixbox.utils.TextGravityFlags

data class FeedBackScreenDataModel(
    @LayoutRes
    var backIconViewResource: Int = R.layout.item_toolbar_feedback,

    @TextGravityFlags
    var screenTitleTextGravity: Int = Gravity.CENTER,
)