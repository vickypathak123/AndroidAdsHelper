package com.example.ads.helper.new_.demo.widget

import android.view.View
import com.example.ads.helper.new_.demo.base.BaseActivity
import com.example.ads.helper.new_.demo.base.BaseDialog
import com.example.ads.helper.new_.demo.base.utils.inflater
import com.example.ads.helper.new_.demo.databinding.DialogSelectLanguageBinding

class SelectLanguageDialog(fActivity: BaseActivity) : BaseDialog<DialogSelectLanguageBinding>(fActivity = fActivity) {
    override fun setBinding(): DialogSelectLanguageBinding = DialogSelectLanguageBinding.inflate(fActivity.inflater)

    private var mUpdateAction: (fLanguageCode: String) -> Unit = {}

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(btnUpdate)
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                btnUpdate -> {
                    mUpdateAction.invoke(spLanguage.selectedItem.toString().substringAfter("(").substringBefore(")").takeIf { it.isNotEmpty() } ?: "en")
                    dismiss()
                }

                else -> {}
            }
        }
    }

    fun show(onUpdateAction: (fLanguageCode: String) -> Unit) {
        if (!fActivity.isFinishing && !isShowing) {
            this.mUpdateAction = onUpdateAction
            super.show()
        }
    }

}