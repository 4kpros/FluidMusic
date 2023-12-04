package com.prosabdev.fluidmusic.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomImageViewRatio11 extends androidx.appcompat.widget.AppCompatImageView {
    public CustomImageViewRatio11(@NonNull Context context) {
        super(context);
    }

    public CustomImageViewRatio11(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageViewRatio11(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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