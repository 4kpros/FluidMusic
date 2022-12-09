package com.prosabdev.fluidmusic.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs

abstract class AnimatorsUtils {

    companion object{
        var mDefaultTranslationPosition: Float = 500.0f

        fun transformScaleViewPager(viewPager: ViewPager2?) {
            if(viewPager == null)
                return
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(5))
            compositePageTransformer.addTransformer { page, position ->
                val normalizedPosition = abs(abs(position) - 1)
                page.alpha = normalizedPosition
                page.scaleX = normalizedPosition / 2 + 0.5f
                page.scaleY = normalizedPosition / 2 + 0.5f
                page.translationX = position * -100
            }
            viewPager.setPadding(0, 0, 0, 0)
            viewPager.setPageTransformer(compositePageTransformer)
        }

        fun crossScaleLeftUp(
            contentView: View, animate: Boolean = false,
            duration: Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            maxAlpha: Float = 1.0f
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        visibility = VISIBLE
                        pivotX = 0f
                        animate()
                            .alpha(maxAlpha)
                            .scaleX(1.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.visibility = VISIBLE
                    contentView.alpha = maxAlpha
                }
            }
        }
        fun crossScaleLeftDown(
            contentView : View,
            animate : Boolean = false,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        pivotX = 0f
                        animate()
                            .alpha(0.0f)
                            .scaleX(0.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.alpha = 0.0f
                }
            }
        }


        fun crossFadeUpClickable(
            contentView: View, animate: Boolean = false,
            duration: Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            maxAlpha: Float = 1.0f
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        animate()
                            .alpha(maxAlpha)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    contentView.isClickable = true
                                }
                            })
                    }
                }else{
                    contentView.isClickable = true
                    contentView.alpha = maxAlpha
                }
            }
        }
        fun crossFadeDownClickable(
            contentView : View,
            animate : Boolean = true,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            minAlpha: Float = 0.35f
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        isClickable = false
                        animate()
                            .alpha(minAlpha)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.isClickable = false
                    contentView.alpha = minAlpha
                }
            }
        }
        fun crossFadeUp(
            contentView: View, animate: Boolean = false,
            duration: Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            maxAlpha: Float = 1.0f
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        visibility = VISIBLE
                        animate()
                            .alpha(maxAlpha)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.visibility = VISIBLE
                    contentView.alpha = maxAlpha
                }
            }
        }
        fun crossFadeDown(
            contentView : View,
            animate : Boolean = false,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        animate()
                            .alpha(0.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.alpha = 0.0f
                }
            }
        }
        fun crossTranslateInFromVertical(
            contentView : View,
            animate : Boolean = false,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        visibility = VISIBLE
                        animate()
                            .translationY(0.0f)
                            .alpha(1.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
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
        fun crossTranslateOutFromVertical(
            contentView : View,
            direction : Int,
            animate : Boolean = false,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            translationDistance: Float = mDefaultTranslationPosition
        ) {
            val tempDirection = if(direction > 0) 1 else -1
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        animate()
                            .translationY(tempDirection * translationDistance)
                            .alpha(0.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.translationY = tempDirection * translationDistance
                    contentView.alpha = 0.0f
                }
            }
        }
        fun crossTranslateInFromHorizontal(
            contentView : View,
            animate : Boolean = false,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
        ) {
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        visibility = VISIBLE
                        animate()
                            .translationX(0.0f)
                            .alpha(1.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
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
        fun crossTranslateOutFromHorizontal(
            contentView : View,
            direction : Int,
            animate : Boolean = false,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            translationDistance: Float = mDefaultTranslationPosition
        ) {
            val tempDirection = if(direction > 0) 1 else -1
            MainScope().launch {
                contentView.clearAnimation()
                if(animate){
                    contentView.apply {
                        animate()
                            .translationX(tempDirection * translationDistance)
                            .alpha(0.0f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.translationX = tempDirection * translationDistance
                    contentView.alpha = 0.0f
                }
            }
        }
    }

}