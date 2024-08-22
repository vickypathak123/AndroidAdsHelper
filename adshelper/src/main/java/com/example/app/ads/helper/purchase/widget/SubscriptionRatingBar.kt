@file:Suppress("unused")

package com.example.app.ads.helper.purchase.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.use
import androidx.core.graphics.drawable.DrawableCompat
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.utils.getColorStateRes
import com.example.app.ads.helper.base.utils.getDrawableRes
import kotlin.math.sign


@SuppressLint("ClickableViewAccessibility")
class SubscriptionRatingBar : View {

    @Suppress("PropertyName")
    val TAG: String = javaClass.simpleName

    @Suppress("PrivatePropertyName")
    @DrawableRes
    private val DEFAULT_FILLED_DRAWABLE: Int = R.drawable.ic_subscribe_star_rating_svg

    @Suppress("PrivatePropertyName")
    @DrawableRes
    private val DEFAULT_EMPTY_DRAWABLE: Int = R.drawable.ic_subscribe_star_rating_svg

    @DrawableRes
    private var filledDrawable = DEFAULT_FILLED_DRAWABLE

    @DrawableRes
    private var emptyDrawable = DEFAULT_EMPTY_DRAWABLE

    private var mStarSize = getPixelValueForDP(20)
    private var mMaxCount = 5
    private var mMinSelectionAllowed = 0
    private var mMargin = getPixelValueForDP(5)
    private var mRating = mMinSelectionAllowed.toFloat()
    private var isIndicator = false
    private var mStepSize = 1f
    private var selectTheTappedRating = false

    private var baseDrawable: Drawable? = null
    private var overlayDrawable: ClipDrawable? = null

    private var progressBackgroundTintColor: ColorStateList? = context.getColorStateRes(R.color.default_view_more_plan_rating_place_holder_color)
    private var progressTintColor: ColorStateList? = context.getColorStateRes(R.color.default_view_more_plan_rating_color)

    //<editor-fold desc="Constructor">
    constructor(context: Context) : super(context) {
        setUpLayout(context = context, attrs = null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUpLayout(context = context, attrs = attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpLayout(context = context, attrs = attrs)
    }
    //</editor-fold>

    private fun setUpLayout(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) {
            mRating = 2.5f
        }
        attrs?.let { attr ->
            context.obtainStyledAttributes(attr, R.styleable.SubscriptionRatingBar, 0, 0).use { osa ->
                (R.styleable.SubscriptionRatingBar_filledDrawable).let {
                    if (osa.hasValue(it)) {
                        filledDrawable = osa.getResourceId(it, DEFAULT_FILLED_DRAWABLE)
                    }
                }
                (R.styleable.SubscriptionRatingBar_emptyDrawable).let {
                    if (osa.hasValue(it)) {
                        emptyDrawable = osa.getResourceId(it, DEFAULT_EMPTY_DRAWABLE)
                    }
                }
                (R.styleable.SubscriptionRatingBar_starSize).let {
                    if (osa.hasValue(it)) {
                        mStarSize = osa.getDimensionPixelSize(it, getPixelValueForDP(20))
                    }
                }
                (R.styleable.SubscriptionRatingBar_numStars).let {
                    if (osa.hasValue(it)) {
                        mMaxCount = osa.getInt(it, 5)
                    }
                }
                (R.styleable.SubscriptionRatingBar_minAllowedStars).let {
                    if (osa.hasValue(it)) {
                        mMinSelectionAllowed = osa.getInt(it, 0)
                    }
                }
                (R.styleable.SubscriptionRatingBar_starSpacing).let {
                    if (osa.hasValue(it)) {
                        mMargin = osa.getDimensionPixelSize(it, getPixelValueForDP(5))
                    }
                }
                (R.styleable.SubscriptionRatingBar_rating).let {
                    if (osa.hasValue(it)) {
                        mRating = osa.getFloat(it, mMinSelectionAllowed.toFloat())
                    }
                }
                (R.styleable.SubscriptionRatingBar_isIndicator).let {
                    if (osa.hasValue(it)) {
                        isIndicator = osa.getBoolean(it, false)
                    }
                }
                (R.styleable.SubscriptionRatingBar_stepSize).let {
                    if (osa.hasValue(it)) {
                        mStepSize = osa.getFloat(it, 1f)
                    }
                }
                (R.styleable.SubscriptionRatingBar_selectTheTappedRating).let {
                    if (osa.hasValue(it)) {
                        selectTheTappedRating = osa.getBoolean(it, false)
                    }
                }
                (R.styleable.SubscriptionRatingBar_progressBackgroundTint).let {
                    if (osa.hasValue(it)) {
                        progressBackgroundTintColor = osa.getColorStateList(it) ?: context.getColorStateRes(R.color.default_view_more_plan_rating_place_holder_color)
                    }
                }
                (R.styleable.SubscriptionRatingBar_progressTint).let {
                    if (osa.hasValue(it)) {
                        progressTintColor = osa.getColorStateList(it) ?: context.getColorStateRes(R.color.default_view_more_plan_rating_color)
                    }
                }
            }
        }

        setEmptyDrawable(emptyDrawable)
        setFilledDrawable(filledDrawable)
        setIsIndicator(isIndicator)
    }

    private fun getPixelValueForDP(dp: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()

    /**
     * Changes the current empty drawable to the one passed in via the
     * `emptyDrawable` drawable.
     */
    private fun setEmptyDrawable(emptyDrawable: Drawable?) {
        var finalDrawable: Drawable? = emptyDrawable
        progressBackgroundTintColor?.let { lColor ->
            emptyDrawable?.let { drawable ->
                DrawableCompat.wrap(drawable).apply {
                    DrawableCompat.setTintList(this, lColor)
                }.also { finalDrawable = it }
            }
        }
//        this.baseDrawable?.setBounds(0, 0, mStarSize, mStarSize)

        this.baseDrawable = finalDrawable.apply {
            this?.setBounds(0, 0, mStarSize, mStarSize)
        }
        postInvalidate()
    }

    /**
     * Changes the current empty drawable to the one passed in via the
     * `emptyDrawable` resource.
     */
    private fun setEmptyDrawable(@DrawableRes emptyDrawable: Int) {
        this.emptyDrawable = emptyDrawable
        setEmptyDrawable(emptyDrawable = context.getDrawableRes(emptyDrawable))
    }

    /**
     * Changes the current filled drawable to the one passed in via the
     * `filledDrawable` drawable.
     */
    private fun setFilledDrawable(filledDrawable: Drawable?) {
        var finalDrawable: Drawable? = filledDrawable
        progressTintColor?.let { lColor ->
            filledDrawable?.let { drawable ->
                DrawableCompat.wrap(drawable).apply {
                    DrawableCompat.setTintList(this, lColor)
                }.also { finalDrawable = it }
            }
        }


        overlayDrawable?.let {
            finalDrawable?.let { fillDrawable ->
                createFilledClipDrawable(fillDrawable)
            } ?: kotlin.run { overlayDrawable = null }
        } ?: kotlin.run {
            finalDrawable?.let { fillDrawable ->
                createFilledClipDrawable(fillDrawable)
            }
        }

        postInvalidate()
    }

    /**
     * Changes the current filled drawable to the one passed in via the
     * `filledDrawable` resource.
     */
    private fun setFilledDrawable(@DrawableRes filledDrawable: Int) {
        setFilledDrawable(filledDrawable = context.getDrawableRes(filledDrawable))
    }

    @SuppressLint("RtlHardcoded")
    private fun createFilledClipDrawable(d: Drawable) {
        overlayDrawable = ClipDrawable(
            d,
            Gravity.LEFT,
            ClipDrawable.HORIZONTAL
        )
        overlayDrawable?.setBounds(0, 0, mStarSize, mStarSize)
    }

    /**
     * Set weather this rating bar is user touch modifiable
     *
     * @param isIndicator if true user cannot change with touch, if false user can change with touch
     */
    private fun setIsIndicator(isIndicator: Boolean) {
        this.isIndicator = isIndicator
        if (this.isIndicator) {
            super.setOnTouchListener(null)
        } else {
            super.setOnTouchListener(mTouchListener)
        }
    }

    private val mTouchListener = OnTouchListener { _, event -> //Basically do not allow user to update this stuff is indicator only
        if (isIndicator) {
            return@OnTouchListener true
        }

        val x = event.x.toInt().toFloat()

        var selectedAmount = 0f

        if (x >= 0 && x <= width) {
            val xPerStar = mMargin * 2 + mStarSize
            if (x < xPerStar * .25f) {
                selectedAmount = 0f
            } else {
                if (mStepSize <= 0) {
                    mStepSize = 0.1f
                }

                selectedAmount = getSelectedRating(x, xPerStar, this@SubscriptionRatingBar.mStepSize)
            }
        }
        if (x < 0) {
            selectedAmount = 0f
        } else if (x > width) {
            selectedAmount = mMaxCount.toFloat()
        }

        setRating(selectedAmount, true)
        true
    }

    /**
     * Given the x-coordinate of a point somewhere within this [SubscriptionRatingBar].
     *
     *
     * Returned value must be an integer multiple of `stepSize`.
     *
     *
     * @param xOfRating x-coordinate (in pixels) of a proposed rating value
     * @param xPerStar  how many pixels (in the x direction) correspond to one star
     * @param stepSize  the smallest individual unit of a rating
     * @return an accepted rating value
     */
    private fun getSelectedRating(xOfRating: Float, xPerStar: Int, stepSize: Float): Float {
        var selectedAmount = (((xOfRating - xPerStar) / xPerStar) + 1)
        val remainder = selectedAmount % stepSize

        selectedAmount -= remainder

        if (selectTheTappedRating) {
            val directionalStep = (sign(remainder.toDouble()) * stepSize).toFloat()
            selectedAmount += directionalStep
        }

        return selectedAmount
    }

    private fun setRating(newRating: Float, fromUser: Boolean = false) {
        var mod = newRating % mStepSize

        // patch up precision issue where this calculation results in a remainder that incorrectly subtracts off the rating.
        if (mod < mStepSize) {
            mod = 0f
        }
        mRating = newRating - mod
        if (mRating < mMinSelectionAllowed) {
            mRating = mMinSelectionAllowed.toFloat()
        } else if (mRating > mMaxCount) {
            mRating = mMaxCount.toFloat()
        }
        postInvalidate()
    }

    /**
     * Sets the current rating, if a rating is set that is not an interval of step size
     * (e.g. 1.2 if stepSize is .5) then we round down to nearest step size
     *
     * @param rating the rating to be set must be positive or 0
     */
    fun setRating(rating: Float) {
        setRating(rating, false)
    }
    fun setProgressTint(@ColorRes fColor: Int) {
        if (progressTintColor != context.getColorStateRes(fColor)) {
            progressTintColor = context.getColorStateRes(fColor)

            setEmptyDrawable(emptyDrawable)
            setFilledDrawable(filledDrawable)
            setIsIndicator(isIndicator)
            postInvalidate()
        }
    }
    fun setProgressTint(fColorStateList: ColorStateList) {
        if (progressTintColor != fColorStateList) {
            progressTintColor = fColorStateList

            setEmptyDrawable(emptyDrawable)
            setFilledDrawable(filledDrawable)
            setIsIndicator(isIndicator)
            postInvalidate()
        }
    }
    fun setProgressBackgroundTintColor(@ColorRes fColor: Int) {
        if (progressBackgroundTintColor != context.getColorStateRes(fColor)) {
            progressBackgroundTintColor = context.getColorStateRes(fColor)

            setEmptyDrawable(emptyDrawable)
            setFilledDrawable(filledDrawable)
            setIsIndicator(isIndicator)
            postInvalidate()
        }
    }
    fun setProgressBackgroundTintColor(fColorStateList: ColorStateList) {
        if (progressBackgroundTintColor != fColorStateList) {
            progressBackgroundTintColor = fColorStateList

            setEmptyDrawable(emptyDrawable)
            setFilledDrawable(filledDrawable)
            setIsIndicator(isIndicator)
            postInvalidate()
        }
    }

    fun setShouldSelectTheTappedRating(selectTheTappedRating: Boolean) {
        this.selectTheTappedRating = selectTheTappedRating
    }

    /**
     * @return Returns the current rating
     */
    fun getRating(): Float {
        return mRating
    }

    /**
     * Sets the stars count
     *
     * @param count amount of stars to draw
     */
    fun setMax(count: Int) {
        mMaxCount = count
        post { requestLayout() }
    }

    /**
     * @return Return star count
     */
    fun getMax(): Int {
        return this.mMaxCount
    }

    /**
     * Sets the minimum allowable stars, so the user cannot swipe to 0 if rating must be at least 1
     *
     * @param minStarCount the minimum amount of stars that have to be selected
     */
    fun setMinimumSelectionAllowed(minStarCount: Int) {
        mMinSelectionAllowed = minStarCount
        postInvalidate()
    }

    /**
     * @return the current min of stars allowed
     */
    fun getMinimumSelectionAllowed(): Int {
        return mMinSelectionAllowed
    }

    /**
     * Sets the stars margins, this is unconnected to starSize, it is a type of padding around each star
     *
     * @param marginInDp the dp size you wish to use
     */
    fun setStarMarginsInDP(marginInDp: Int) {
        setStarMargins(getPixelValueForDP(marginInDp))
    }

    /**
     * Sets the star margins in PIXELS
     *
     * @param margins margins in Pixels
     */
    private fun setStarMargins(margins: Int) {
        this.mMargin = margins
        post { requestLayout() }
    }

    /**
     * @return returns current margins in pixels
     */
    fun getMargin(): Int {
        return this.mMargin
    }

    /**
     * Sets the square box for star size
     *
     * @param size the dp version of size
     */
    fun setStarSizeInDp(size: Int) {
        setStarSize(getPixelValueForDP(size))
    }

    /**
     * Sets the square box for star size
     *
     * @param size pixels for 1 side of square box
     */
    private fun setStarSize(size: Int) {
        mStarSize = size
        baseDrawable?.setBounds(0, 0, mStarSize, mStarSize)
        overlayDrawable?.setBounds(0, 0, mStarSize, mStarSize)
        post { requestLayout() }
    }

    override fun setOnTouchListener(l: OnTouchListener?) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //Currently we don't care about wrap_content, and other stuff
        val height = mMargin * 2 + mStarSize
        val width = height * mMaxCount

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var movedX = 0f
        canvas.translate(0f, mMargin.toFloat())
        var remaining = mRating
        for (i in 0 until mMaxCount) {
            canvas.translate(mMargin.toFloat(), 0f)
            movedX += mMargin.toFloat()

            baseDrawable?.draw(canvas)

            overlayDrawable?.let { overDrawable ->
                if (remaining >= 1) {
                    overDrawable.setLevel(10000)
                    overDrawable.draw(canvas)
                } else if (remaining > 0) {
                    overDrawable.setLevel((remaining * 10000).toInt())
                    overDrawable.draw(canvas)
                } else {
                    overDrawable.setLevel(0)
                }
                remaining -= 1f
            }

            canvas.translate(mStarSize.toFloat(), 0f)
            movedX += mStarSize.toFloat()

            canvas.translate(mMargin.toFloat(), 0f)
            movedX += mMargin.toFloat()
        }

        canvas.translate(movedX * -1, (mMargin * -1).toFloat())
    }


}