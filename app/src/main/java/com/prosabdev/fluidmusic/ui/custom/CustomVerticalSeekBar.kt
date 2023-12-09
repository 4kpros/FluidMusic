package com.prosabdev.fluidmusic.ui.custom

import android.content.Context
import android.util.AttributeSet
import com.lukelorusso.verticalseekbar.VerticalSeekBar

class CustomVerticalSeekBar : VerticalSeekBar {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }
}
