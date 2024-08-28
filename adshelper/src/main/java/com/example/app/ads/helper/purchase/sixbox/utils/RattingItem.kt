package com.example.app.ads.helper.purchase.sixbox.utils

import android.view.Gravity
import androidx.annotation.IntDef
import androidx.annotation.StringRes


data class RattingItem(
    @StringRes
    var title: Int,
    @StringRes
    var subTitle: Int,
    @StringRes
    var givenBy: Int,

    var rattingStar: Float = 5.0f,

    @TextGravityFlags
    var givenByTextGravity: Int = Gravity.END
)


@IntDef(
    flag = true, value = [
        Gravity.START,
        Gravity.END,
        Gravity.CENTER,
    ]
)
annotation class TextGravityFlags