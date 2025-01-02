package com.example.app.ads.helper.purchase.fourplan.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.ads.helper.base.utils.getDrawableRes
import com.example.app.ads.helper.base.utils.inflater
import com.example.app.ads.helper.databinding.FourPlanLayoutItemBoxBinding
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanUserItem
import com.example.app.ads.helper.utils.setIncludeFontPaddingFlag

class FourPlanUserItemAdapter(
    val itemList: ArrayList<FourPlanUserItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class FourPlanUserItemViewHolder(val fBinding: FourPlanLayoutItemBoxBinding) : RecyclerView.ViewHolder(fBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FourPlanUserItemViewHolder(fBinding = FourPlanLayoutItemBoxBinding.inflate(parent.context.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position in 0..<itemCount) {
            if (holder is FourPlanUserItemViewHolder) {
                with(holder) {
                    with(fBinding) {
                        setIncludeFontPaddingFlag(fView = root, fLanguageCode = SUBSCRIPTION_DATA_LANGUAGE_CODE.takeIf { it.isNotEmpty() } ?: "en")
                        val finalPosition = (position % itemList.size)
                        with(itemList[finalPosition]) {
                            ivIcon.apply {
                                this.setBackgroundDrawable(itemView.context.getDrawableRes(backgroundDrawable))
                                this.setImageDrawable(itemView.context.getDrawableRes(itemIcon))
                            }

                            txtName.text = getLocalizedString<String>(
                                context = itemView.context,
                                resourceId = itemName,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }
}