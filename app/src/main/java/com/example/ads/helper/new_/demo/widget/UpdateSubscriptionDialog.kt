package com.example.ads.helper.new_.demo.widget

import android.content.res.ColorStateList
import android.view.View
import com.example.ads.helper.new_.demo.base.BaseActivity
import com.example.ads.helper.new_.demo.base.BaseDialog
import com.example.ads.helper.new_.demo.base.utils.inflater
import com.example.ads.helper.new_.demo.databinding.DialogUpdateSubscriptionBinding

class UpdateSubscriptionDialog(fActivity: BaseActivity) : BaseDialog<DialogUpdateSubscriptionBinding>(fActivity = fActivity) {
    override fun setBinding(): DialogUpdateSubscriptionBinding = DialogUpdateSubscriptionBinding.inflate(fActivity.inflater)

    private var mUpdateAction: (
        fLanguage: String,
        useInstantAccessLottieFile: Boolean,
        isWithSliderAnimation: Boolean,
        fTimeLineMainColor: ColorStateList?,
        fTimeLineHeaderColor: ColorStateList?,
        fTimeLineCloseIconColor: ColorStateList?,
        fTimeLineTrackInactiveColor: ColorStateList?,
        fTimeLineHintTextColor: ColorStateList?,
        fTimeLineInstantAccessHintTextColor: ColorStateList?,
        fSecureWithPlayStoreTextColor: ColorStateList?,
        fSecureWithPlayStoreBackgroundColor: ColorStateList?,
        fButtonContinueTextColor: ColorStateList?,
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _ -> }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(btnCancel, btnUpdate)
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                btnCancel -> dismiss()
                btnUpdate -> {
                    mUpdateAction(
                        spLanguage.selectedItem.toString(),
                        useInstantAccessLottieFile.isChecked,
                        withSliderAnimation.isChecked,
                        ColorStateList.valueOf(colorPikerTimeLineMainColor.selectedColor).takeIf { txtTimeLineMainColor.isChecked },
                        ColorStateList.valueOf(colorPikerTimeLineHeaderColor.selectedColor).takeIf { txtTimeLineHeaderColor.isChecked },
                        ColorStateList.valueOf(colorPikerCloseIcon.selectedColor).takeIf { txtCloseIconColor.isChecked },
                        ColorStateList.valueOf(colorPikerTrackInactive.selectedColor).takeIf { txtTrackInactiveColor.isChecked },
                        ColorStateList.valueOf(colorPikerTimeLineHintTextColor.selectedColor).takeIf { txtTimeLineHintTextColor.isChecked },
                        ColorStateList.valueOf(colorPikerInstantAccessHintTextColor.selectedColor).takeIf { txtInstantAccessHintTextColor.isChecked },
                        ColorStateList.valueOf(colorPikerSecureWithPlayStoreTextColor.selectedColor).takeIf { txtSecureWithPlayStoreTextColor.isChecked },
                        ColorStateList.valueOf(colorPikerSecureWithPlayStoreBackgroundColor.selectedColor).takeIf { txtSecureWithPlayStoreBackgroundColor.isChecked },
                        ColorStateList.valueOf(colorPikerButtonContinueTextColor.selectedColor).takeIf { txtButtonContinueTextColor.isChecked },
                    )
                }

                else -> {}
            }
        }
    }

    fun show(
        onUpdateAction: (
            fLanguage: String,
            useInstantAccessLottieFile: Boolean,
            isWithSliderAnimation: Boolean,
            fTimeLineMainColor: ColorStateList?,
            fTimeLineHeaderColor: ColorStateList?,
            fTimeLineCloseIconColor: ColorStateList?,
            fTimeLineTrackInactiveColor: ColorStateList?,
            fTimeLineHintTextColor: ColorStateList?,
            fTimeLineInstantAccessHintTextColor: ColorStateList?,
            fSecureWithPlayStoreTextColor: ColorStateList?,
            fSecureWithPlayStoreBackgroundColor: ColorStateList?,
            fButtonContinueTextColor: ColorStateList?,
        ) -> Unit
    ) {
        if (!fActivity.isFinishing && !isShowing) {
            this.mUpdateAction = onUpdateAction
            super.show()
        }
    }

}