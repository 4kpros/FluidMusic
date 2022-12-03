package com.prosabdev.fluidmusic.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;

public class CustomShapeableImageViewImageViewRatio11 extends ShapeableImageView {
    public CustomShapeableImageViewImageViewRatio11(Context context) {
        super(context);
    }

    public CustomShapeableImageViewImageViewRatio11(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomShapeableImageViewImageViewRatio11(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight(); // Optimization so we don't measure twice unless we need to
        if (width != height) {
            setMeasuredDimension(width, width);
        }
    }


}
