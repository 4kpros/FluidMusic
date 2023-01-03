package com.prosabdev.fluidmusic.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lukelorusso.verticalseekbar.VerticalSeekBar;

public class CustomVerticalSeekBar extends VerticalSeekBar {
    public CustomVerticalSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVerticalSeekBar(@NonNull Context context) {
        super(context);
    }

    public CustomVerticalSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }
}
