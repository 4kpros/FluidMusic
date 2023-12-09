package com.prosabdev.fluidmusic.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.l4digital.fastscroll.FastScroller

class CustomFastScroller : FastScroller {
    constructor(context: Context) : super(context)
    constructor(context: Context, size: Size?) : super(context, size)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        //super.setLayoutParams(params);
    }

    override fun setLayoutParams(viewGroup: ViewGroup) {
        super.setLayoutParams(viewGroup)
    }
}
