package com.example.app.ads.helper.purchase.sixbox.utils

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.inflater
import com.example.app.ads.helper.databinding.CustomInfinitePagerLayoutBinding
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.setIncludeFontPaddingFlag

class InfiniteRecyclerAdapter(
    originalList: List<RattingItem>,
    private val ratingHeaderColor: ColorStateList,
    private val ratingSubHeaderColor: ColorStateList,
    private val ratingProgressTintColor: ColorStateList,
    private val ratingProgressBackgroundTintColor: ColorStateList,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val newList: List<RattingItem> =
        listOf(originalList.last()) + originalList + listOf(originalList.first())

    private inner class InfiniteRecyclerViewHolder(val fBinding: CustomInfinitePagerLayoutBinding) : RecyclerView.ViewHolder(fBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InfiniteRecyclerViewHolder(fBinding = CustomInfinitePagerLayoutBinding.inflate(parent.context.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position in 0..<itemCount) {
            if (holder is InfiniteRecyclerViewHolder) {
                with(holder) {
                    with(fBinding) {
                        setIncludeFontPaddingFlag(fView = root, fLanguageCode = SUBSCRIPTION_DATA_LANGUAGE_CODE.takeIf { it.isNotEmpty() } ?: "en")
                        with(newList[position]) {
                            ratingBar.apply {
                                this.setRating(rating = rattingStar)
                                this.setProgressTint(fColorStateList = ratingProgressTintColor)
                                this.setProgressBackgroundTintColor(fColorStateList = ratingProgressBackgroundTintColor)
                            }
                            txtRatingHeader.apply {
                                this.text = getLocalizedString<String>(
                                    context = itemView.context,
                                    resourceId = title,
                                )
                                this.setTextColor(ratingHeaderColor)
                            }
                            txtRating.apply {
                                this.text = getLocalizedString<String>(
                                    context = itemView.context,
                                    resourceId = subTitle,
                                )
                                this.setTextColor(ratingSubHeaderColor)
                            }
                            txtRatingBy.apply {
                                this.text = getLocalizedString<String>(
                                    context = itemView.context,
                                    resourceId = R.string.rating_by,
                                    formatArgs = arrayOf(
                                        getLocalizedString<String>(
                                            context = itemView.context,
                                            resourceId = givenBy,
                                        )
                                    )
                                )
                                this.setTextColor(ratingSubHeaderColor)

                                this.gravity = when (givenByTextGravity) {
                                    Gravity.CENTER -> Gravity.CENTER
                                    Gravity.START -> Gravity.CENTER_VERTICAL or Gravity.START
                                    else -> Gravity.CENTER_VERTICAL or Gravity.END
                                }
                            }
                        }
                        root.post {
                            root.requestLayout()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return newList.size
    }
}