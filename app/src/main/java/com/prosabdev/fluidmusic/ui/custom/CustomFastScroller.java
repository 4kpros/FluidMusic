package com.prosabdev.fluidmusic.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.l4digital.fastscroll.FastScroller;

public class CustomFastScroller extends FastScroller {
    public CustomFastScroller(@NonNull Context context) {
        super(context);
    }

    public CustomFastScroller(@NonNull Context context, Size size) {
        super(context, size);
    }

    public CustomFastScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFastScroller(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setLayoutParams(@NonNull ViewGroup.LayoutParams params) {
        //super.setLayoutParams(params);
    }

    @Override
    public void setLayoutParams(@NonNull ViewGroup viewGroup) {
        super.setLayoutParams(viewGroup);
    }
}
