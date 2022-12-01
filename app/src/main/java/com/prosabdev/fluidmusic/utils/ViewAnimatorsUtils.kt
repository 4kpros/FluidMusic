package com.prosabdev.fluidmusic.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs

abstract class ViewAnimatorsUtils {

    companion object{
        var defaultTranslationPosition: Float = 500.0f

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

        fun crossFadeUpClickable(
            contentView: View, animate: Boolean = false,
            duration: Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            maxAlpha: Float = 1.0f
        ) {
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        contentView.isClickable = true
                        animate()
                            .alpha(maxAlpha)
                            .setInterpolator(DecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(null)
                    }
                }else{
                    contentView.isClickable = true
                    contentView.alpha = maxAlpha
                }
            }
        }
        fun crossFadeDownClickable(
            contentView : View,
            animate : Boolean = false,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            minAlpha: Float = 0.35f
        ) {
            MainScope().launch {
                if(animate){
                    contentView.apply {
                        alpha = 1.0f
                        animate()
                            .alpha(minAlpha)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(duration.toLong())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    contentView.isClickable = false
                                }
                            })
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
                if(animate){
                    contentView.apply {
                        alpha = 0f
                        visibility = VISIBLE
                        animate()
                            .alpha(maxAlpha)
                            .setInterpolator(DecelerateInterpolator())
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
                if(animate){
                    contentView.apply {
                        animate()
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
                    contentView.visibility = GONE
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
            duration: Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)
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