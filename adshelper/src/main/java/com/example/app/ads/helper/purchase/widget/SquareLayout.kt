package com.example.app.ads.helper.purchase.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout


/**
 * @author Akshay Harsoda
 * @since 01 Aug 2024
 *
 * SquareLayout.kt - Simple view which has load in square
 */
class SquareLayout : FrameLayout {

    //<editor-fold desc="Constructor">
    constructor(context: Context) : super(context) {
        setUpLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUpLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUpLayout()
    }
    //</editor-fold>

    private fun setUpLayout() {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec)

        val height = measuredHeight

        val size = height.toDouble().toInt()
        setMeasuredDimension(size, size)

    }
}