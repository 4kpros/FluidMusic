package com.prosabdev.fluidmusic.utils

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class CustomAnimators {

    companion object{
        var defaultTranslationPosition: Float = 500.0f

        fun crossFadeUp(
            contentView: View, animate: Boolean = false,
            duration: Int = contentView.resources.getInteger(R.integer.config_shortAnimTime),
            maxAlpha: Float = 1.0f
        ) {
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        alpha = 0f
                        visibility = View.VISIBLE
                        animate()
                            .alpha(maxAlpha)
                            .setInterpolator(DecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.visibility = View.VISIBLE
                    contentView.alpha = maxAlpha
                }
            }
        }
        fun crossFadeDown(contentView : View, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            MainScope().launch {
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
        }
        fun crossTranslateInFromVertical(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime), translationDistance: Float = defaultTranslationPosition) {
            val tempDirection = if(direction > 0) 1 else -1
            Log.i(ConstantValues.TAG, "Show")
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        translationY = tempDirection * translationDistance
                        alpha = 0.0f
                        visibility = VISIBLE
                        animate()
                            .translationY(0.0f)
                            .alpha(1.0f)
                            .setInterpolator(DecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.translationY = 0.0f
                    contentView.alpha = 1.0f
                    contentView.visibility = VISIBLE
                }
            }
        }
        fun crossTranslateOutFromVertical(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime), translationDistance: Float = defaultTranslationPosition) {
            val tempDirection = if(direction > 0) 1 else -1
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        translationY = 0.0f
                        alpha = 1.0f
                        visibility = VISIBLE
                        animate()
                            .translationY(tempDirection * translationDistance)
                            .alpha(0.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    contentView.visibility = GONE
                                }
                            })
                    }
                }else{
                    contentView.translationY = tempDirection * translationDistance
                    contentView.alpha = 0.0f
                    contentView.visibility = GONE
                }
            }
        }
        fun crossTranslateInFromHorizontal(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime), translationDistance: Float = defaultTranslationPosition) {
            val tempDirection = if(direction > 0) 1 else -1
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        translationX = tempDirection * translationDistance
                        alpha = 0.0f
                        visibility = VISIBLE
                        animate()
                            .translationX(0.0f)
                            .alpha(1.0f)
                            .setInterpolator(DecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.translationX = 0.0f
                    contentView.alpha = 1.0f
                    contentView.visibility = VISIBLE
                }
            }
        }
        fun crossTranslateOutFromHorizontal(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime), translationDistance: Float = defaultTranslationPosition) {
            val tempDirection = if(direction > 0) 1 else -1
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        animate()
                            .translationX(tempDirection * translationDistance)
                            .alpha(0.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    contentView.visibility = GONE
                                }
                            })
                    }
                }else{
                    contentView.translationX = tempDirection * translationDistance
                    contentView.alpha = 0.0f
                    contentView.visibility = GONE
                }
            }
        }
        fun crossScaleYUp(contentView : View, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        scaleY = 0.0f
                        animate()
                            .scaleY(1.0f)
                            .setInterpolator(DecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.scaleY = 1.0f
                }
            }
        }
        fun crossScaleYDown(
            contentView: View, animate: Boolean = false,
            duration: Int = contentView.resources.getInteger(R.integer.config_shortAnimTime)
        ) {
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        animate()
                            .scaleY(0.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.scaleY = 0.0f
                }
            }
        }
    }

}