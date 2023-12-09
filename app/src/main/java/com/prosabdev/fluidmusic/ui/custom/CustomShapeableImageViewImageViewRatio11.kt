package com.prosabdev.fluidmusic.ui.custom

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView

class CustomShapeableImageViewImageViewRatio11 : ShapeableImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight // Optimization so we don't measure twice unless we need to
        if (width != height) {
            setMeasuredDimension(width, width)
        }
    }
}
