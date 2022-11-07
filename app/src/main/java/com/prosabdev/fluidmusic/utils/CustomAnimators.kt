package com.prosabdev.fluidmusic.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class CustomAnimators {

    companion object{
        fun hideLoadingView(contentView : View, loadingView : View, delayTime : Long = 250, animate : Boolean = false) {
            MainScope().launch {
                delay(delayTime)
                crossFadeDown(loadingView, animate, 100)
                crossFadeUp(contentView, animate, 200)
            }
        }
        fun showLoadingView(contentView : View, loadingView : View, animate : Boolean = false) {
            crossFadeDown(contentView, animate, 100)
            crossFadeUp(loadingView, animate, 200)
        }
        fun crossFadeUp(contentView : View, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            if(animate){
                contentView.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(duration.toLong())
                        .setListener(null)
                }
            }else{
                contentView.visibility = View.VISIBLE
                contentView.alpha = 1.0f
            }
        }
        fun crossFadeDown(contentView : View, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            if(animate){
                contentView.apply {
                    animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(duration.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                contentView.visibility = View.GONE
                            }
                        })
                }
            }else{
                contentView.visibility = View.GONE
                contentView.alpha = 0.0f
            }
        }
        fun crossScaleIn(contentView : View, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            return
            if(animate){
                val mShortAnimationDuration = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
                contentView.apply {
                    scaleX = 1f
                    scaleY = 1f
                    animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(duration.toLong())
                        .setListener(null)
                }
            }else{
                contentView.scaleX = 0.9f
                contentView.scaleY = 0.9f
            }
        }
        fun crossScaleOut(contentView : View, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            return
            if(animate){
                contentView.apply {
                    scaleX = 0.9f
                    scaleY = 0.9f
                    animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(duration.toLong())
                        .setListener(null)
                }
            }else{
                contentView.scaleX = 1.0f
                contentView.scaleY = 1.0f
            }
        }
    }

}