package com.prosabdev.fluidmusic.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

abstract class CustomAnimators {

    companion object{

        fun hideLoadingView(viewToHide : View, loadingView : View, animate : Boolean = false) {
            if(animate){
                crossFadeLoadingViewToContentView(viewToHide, loadingView)
            }else{
                viewToHide.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
            }
        }
        private fun crossFadeLoadingViewToContentView(contentView : View, loadingView : View) {
            val mShortAnimationDuration = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
            //Animate content view to show
            contentView.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setDuration(mShortAnimationDuration.toLong())
                    .setListener(null)
            }
            //Animate loading view to hide
            loadingView.animate()
                .alpha(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(mShortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        loadingView.visibility = View.GONE
                    }
                })
        }

        fun crossFadeUp(contentView : View, animate : Boolean = false) {
            if(animate){
                val mShortAnimationDuration = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
                contentView.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(mShortAnimationDuration.toLong())
                        .setListener(null)
                }
            }else{
                contentView.visibility = View.VISIBLE
            }
        }
        fun crossFadeDown(contentView : View, animate : Boolean = false) {
            if(animate){
                val mShortAnimationDuration = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
                contentView.apply {
                    animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(mShortAnimationDuration.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                contentView.visibility = View.GONE
                            }
                        })
                }
            }else{
                contentView.visibility = View.GONE
            }
        }
        fun crossScaleIn(contentView : View, animate : Boolean = false) {
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
                        .setDuration(mShortAnimationDuration.toLong())
                        .setListener(null)
                }
            }else{
                contentView.scaleX = 0.9f
                contentView.scaleY = 0.9f
            }
        }
        fun crossScaleOut(contentView : View, animate : Boolean = false) {
            return
            if(animate){
                val mShortAnimationDuration = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
                contentView.apply {
                    scaleX = 0.9f
                    scaleY = 0.9f
                    animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(mShortAnimationDuration.toLong())
                        .setListener(null)
                }
            }else{
                contentView.scaleX = 1.0f
                contentView.scaleY = 1.0f
            }
        }
    }

}