package com.prosabdev.fluidmusic.utils

import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

abstract class ViewInsetModifiersUtils {
    companion object{

        fun updateTopViewInsets(view: View) {
            view.setOnApplyWindowInsetsListener { view, insets ->
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    view.updatePadding(
                        top = insets.getInsets(WindowInsetsCompat.Type.systemGestures()).top,
                    )
                }else{
                    view.updatePadding(
                        top = insets.systemWindowInsetTop,
                    )
                }
                insets
            }
        }
        fun updateBottomViewInsets(view: View) {
            view.setOnApplyWindowInsetsListener { view, insets ->
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    view.updatePadding(
                        bottom = insets.getInsets(WindowInsetsCompat.Type.systemGestures()).bottom,
                    )
                }else{
                    view.updatePadding(
                        bottom = insets.systemWindowInsetBottom,
                    )
                }
                insets
            }
        }
        fun removeBottomViewInsets(view: View) {
            view.setOnApplyWindowInsetsListener { view, insets ->
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    view.updatePadding(
                        bottom = 0,
                    )
                }else{
                    view.updatePadding(
                        bottom = 0,
                    )
                }
                insets
            }
        }
        fun updateBottomViewInsetsWithoutChild(view: View) {
            ViewCompat.setOnApplyWindowInsetsListener(
                view
            ) { view: View, windowInsets: WindowInsetsCompat ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(
                    bottom = insets.bottom,
                )
                // Return CONSUMED if you don't want the window insets to keep being
                // passed down to descendant views.
                WindowInsetsCompat.CONSUMED
            }
        }
    }
}