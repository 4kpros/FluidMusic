package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R

class CustomGridItemDecoration(context: Context, private val mSpanCount : Int) : RecyclerView.ItemDecoration() {

    var mDecorationMargin : Int = context.resources.getDimensionPixelSize(R.dimen.main_medium_margin)
    var mDecorationSpacing : Int = context.resources.getDimensionPixelSize(R.dimen.main_small_margin)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition: Int =
            parent.getChildAdapterPosition(
                view
            )
        val totalCount: Int =
            parent.adapter?.itemCount ?: 0

        if (itemPosition >= 1 && itemPosition < totalCount - 1) {
            //Add space to first and last item on every row
            if(mSpanCount >= 1){
                if(itemPosition < mSpanCount) {
                    if(itemPosition == 1){
                        outRect.left = mDecorationMargin
                        if(mSpanCount == 2){
                            outRect.right = mDecorationSpacing
                        }
                    }else{
                        outRect.left = mDecorationSpacing
                        outRect.right = mDecorationSpacing
                    }
                }else{
                    if(itemPosition % mSpanCount == 0){
                        outRect.right = mDecorationMargin
                    }else if(itemPosition % mSpanCount == 1){
                        outRect.left = mDecorationMargin
                    }else{
                        //Here add space between items
                        outRect.left = mDecorationSpacing
                        outRect.right = mDecorationSpacing
                    }
                }
            }else{
                outRect.left = mDecorationMargin
                outRect.right = mDecorationMargin
            }
        }
    }
}