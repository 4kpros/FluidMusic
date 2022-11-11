package com.prosabdev.fluidmusic.utils

import android.R
import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class CustomAnimators {

    companion object{
        var defaultTranslationPosition: Float = 1000.0f

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
        fun crossFadeUp(
            contentView: View, animate: Boolean = false,
            duration: Int = contentView.resources.getInteger(R.integer.config_shortAnimTime),
            maxAlpha: Float = 1.0f
        ) {
            if(animate){
                contentView.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(maxAlpha)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .setDuration(duration.toLong())
                        .setListener(null)
                }
            }else{
                contentView.visibility = View.VISIBLE
                contentView.alpha = maxAlpha
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

        fun crossTranslateInFromVertical(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            val tempDirection = if(direction > 0) 1 else -1
            if(animate){
                contentView.apply {
                    translationY = tempDirection * defaultTranslationPosition
                    alpha = 0.5f
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
        fun crossTranslateInFromHorizontal(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            val tempDirection = if(direction > 0) 1 else -1
            if(animate){
                contentView.apply {
                    translationX = tempDirection * defaultTranslationPosition
                    alpha = 0.5f
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
        fun crossTranslateOutFromVertical(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            val tempDirection = if(direction > 0) 1 else -1
            if(animate){
                contentView.apply {
                    animate()
                        .translationY(tempDirection * defaultTranslationPosition)
                        .alpha(0.0f)
                        .setInterpolator(AccelerateInterpolator())
                        .setDuration(duration.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                contentView.visibility = View.GONE
                            }
                        })
                }
            }else{
                contentView.translationY = tempDirection * defaultTranslationPosition
                contentView.alpha = 0.0f
                contentView.visibility = GONE
            }
        }
        fun crossTranslateOutFromHorizontal(contentView : View, direction : Int, animate : Boolean = false, duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime)) {
            val tempDirection = if(direction > 0) 1 else -1
            if(animate){
                contentView.apply {
                    animate()
                        .translationX(tempDirection * defaultTranslationPosition)
                        .alpha(0.0f)
                        .setInterpolator(AccelerateInterpolator())
                        .setDuration(duration.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                contentView.visibility = View.GONE
                            }
                        })
                }
            }else{
                contentView.translationX = tempDirection * defaultTranslationPosition
                contentView.alpha = 0.0f
                contentView.visibility = GONE
            }
        }

        suspend fun animateCrossFadeOutInTextView(
            textView: AppCompatTextView?,
            textValue : String,
            durationInterval : Int = 150
        ) {
            textView?.apply {
                View.VISIBLE
                animate()
                    .alpha(0f)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .setDuration(durationInterval.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            textView.text = textValue
                            crossFadeUp(textView as View, true, durationInterval)
                        }
                    })
            }
        }
        fun animateCrossFadeOutInImage(
            context : Context,
            imageView: ImageView?,
            tempBinary: ByteArray?,
            blurred : Boolean = false,
            width : Int = 100,
            durationInterval : Int = 100
        ) {
            imageView?.apply {
                animate()
                    .alpha(1.0f)
                    .setInterpolator(DecelerateInterpolator())
                    .setDuration(durationInterval.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            if(!blurred){
                                CustomUILoaders.loadCovertArtFromBinaryData(context, imageView, tempBinary, width)
                            }else{
                                CustomUILoaders.loadWithBinaryDataWithCrossFade(context, imageView, tempBinary, width)
                            }
                        }
                    })
            }
        }
    }

}