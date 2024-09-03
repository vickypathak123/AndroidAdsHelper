package com.example.app.ads.helper.purchase.fourplan.adapter

import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.getDrawableRes
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.inflater
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.databinding.FourPlanRatingItemBinding
import com.example.app.ads.helper.getLocalizedString
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItem
import com.example.app.ads.helper.purchase.fourplan.utils.FourPlanRattingItemType
import java.text.NumberFormat
import java.util.Locale

class FourPlanRatingAdapter(
    originalList: List<FourPlanRattingItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @Suppress("PropertyName")
    val TAG: String = javaClass.simpleName

    private val newList: List<FourPlanRattingItem> =
        listOf(originalList.last()) + originalList + listOf(originalList.first())

    private inner class FourPlanRatingViewHolder(val fBinding: FourPlanRatingItemBinding) : RecyclerView.ViewHolder(fBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FourPlanRatingViewHolder(fBinding = FourPlanRatingItemBinding.inflate(parent.context.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position in 0..<itemCount) {
            if (holder is FourPlanRatingViewHolder) {
                with(holder) {
                    with(fBinding) {
                        with(newList[position]) {
                            when (itemType) {
                                FourPlanRattingItemType.APP_RATING -> {
                                    groupOfAppRating.visible
                                    groupOfCustomerCount.gone
                                    groupOfReviewViews.gone
                                    ivQuotes.visible

                                    txtAppRatingCount.text = ratingCount.toString()

                                    txtAppRatingHint.text = getLocalizedString<String>(
                                        context = itemView.context,
                                        resourceId = R.string.rating
                                    )

                                    txtBestAppHeader.text = getLocalizedString<String>(
                                        context = itemView.context,
                                        resourceId = ratingHeader.takeIf { it > 0 } ?: R.string.rating_header
                                    )
                                    txtBestAppSubHeader.text = getLocalizedString<String>(
                                        context = itemView.context,
                                        resourceId = ratingSubHeader.takeIf { it > 0 } ?: R.string.rating_sub_header
                                    )
                                }

                                FourPlanRattingItemType.SATISFIED_CUSTOMER -> {
                                    groupOfAppRating.gone
                                    groupOfCustomerCount.visible
                                    groupOfReviewViews.gone
                                    ivQuotes.gone

                                    txtCustomerCount.text = NumberFormat.getNumberInstance(Locale("en", "IN")).format(satisfiedCustomerCount)
                                    txtSatisfiedCustomer.text = getLocalizedString<String>(
                                        context = itemView.context,
                                        resourceId = R.string.satisfied_customer
                                    )

                                    ivGirl.setImageDrawable(itemView.context.getDrawableRes(satisfiedCustomerDrawable.takeIf { it > 0 } ?: R.drawable.aa_test_image_girl))
                                }

                                FourPlanRattingItemType.REVIEW -> {
                                    groupOfAppRating.gone
                                    groupOfCustomerCount.gone
                                    groupOfReviewViews.visible
                                    ivQuotes.visible

                                    txtReviewHeader.apply {
                                        this.text = getLocalizedString<String>(
                                            context = itemView.context,
                                            resourceId = reviewTitle.takeIf { it > 0 } ?: R.string.rating_header,
                                        )
                                    }
                                    txtReview.apply {
                                        this.text = getLocalizedString<String>(
                                            context = itemView.context,
                                            resourceId = reviewSubTitle.takeIf { it > 0 } ?: R.string.rating_sub_header,
                                        )
                                    }
                                    txtReviewBy.apply {
                                        this.text = getLocalizedString<String>(
                                            context = itemView.context,
                                            resourceId = R.string.rating_by,
                                            formatArgs = arrayOf(
                                                getLocalizedString<String>(
                                                    context = itemView.context,
                                                    resourceId = reviewGivenBy.takeIf { it > 0 } ?: R.string.rating_user,
                                                )
                                            )
                                        )

                                        this.gravity = when (reviewGivenByTextGravity) {
                                            Gravity.CENTER -> Gravity.CENTER
                                            Gravity.START -> Gravity.CENTER_VERTICAL or Gravity.START
                                            else -> Gravity.CENTER_VERTICAL or Gravity.END
                                        }
                                    }
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