package com.prosabdev.common.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Camera
import android.graphics.Matrix
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs

abstract class AnimatorsUtils {

    companion object{
        private const val DEFAULT_TRANSLATION_VALUE: Float = 500.0f

//        private const val MIN_SCALE_PAGE_TRANSFORMER = 0.65f
        private const val ROTATION_PAGE_TRANSFORMER = 15f
        private const val TRANSLATION_X_PAGE_TRANSFORMER = 50f

        private val MATRIX_OFFSET: Matrix = Matrix()
        private val CAMERA_OFFSET: Camera = Camera()
        private val TEMP_FLOAT_OFFSET = FloatArray(2)

        fun applyPageTransformer(viewPager: ViewPager2?) {
            if(viewPager == null)
                return
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer { view, position ->
                view.apply {
                    val tempNormalized: Float = if (position < 0) position + 1f else abs(1f - position)
//                    val finalScale = if(tempNormalized < MIN_SCALE_PAGE_TRANSFORMER) MIN_SCALE_PAGE_TRANSFORMER else tempNormalized
//                    scaleX = finalScale
//                    scaleY = finalScale
                    alpha = tempNormalized

                    val tempRotation: Float = (if (position < 0) ROTATION_PAGE_TRANSFORMER else -ROTATION_PAGE_TRANSFORMER) * abs(position)
                    val tempTranslation: Float = (if (position < 0) TRANSLATION_X_PAGE_TRANSFORMER else -TRANSLATION_X_PAGE_TRANSFORMER) * abs(position)
                     translationX = getOffsetX(tempTranslation, width, height)
                     pivotX = width * 0.5f
                     pivotY = 0f
                     rotationY = tempRotation
                }
            }
            viewPager.setPageTransformer(compositePageTransformer)
        }

        private fun getOffsetX(rotation: Float, width: Int, height: Int): Float {
            MATRIX_OFFSET.reset()
            CAMERA_OFFSET.save()
            CAMERA_OFFSET.rotateY(abs(rotation))
            CAMERA_OFFSET.getMatrix(MATRIX_OFFSET)
            CAMERA_OFFSET.restore()
            MATRIX_OFFSET.preTranslate(-width * 0.5f, -height * 0.5f)
            MATRIX_OFFSET.postTranslate(width * 0.5f, height * 0.5f)
            TEMP_FLOAT_OFFSET[0] = width.toFloat()
            TEMP_FLOAT_OFFSET[1] = height.toFloat()
            MATRIX_OFFSET.mapPoints(TEMP_FLOAT_OFFSET)
            return (width - TEMP_FLOAT_OFFSET[0]) * if (rotation > 0.0f) 1.0f else -1.0f
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
            maxAlpha: Float = 1.0f,
            enableView: Boolean = true
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
                                    if(enableView){
                                        contentView.isEnabled = true
                                    }
                                    contentView.isClickable = true
                                }
                            })
                    }
                }else{
                    if(enableView){
                        contentView.isEnabled = true
                    }
                    contentView.isClickable = true
                    contentView.alpha = maxAlpha
                }
            }
        }
        fun crossFadeDownClickable(
            contentView : View,
            animate : Boolean = true,
            duration : Int = contentView.resources.getInteger(android.R.integer.config_shortAnimTime),
            minAlpha: Float = 0.35f,
            enableView: Boolean = true
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
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    if(enableView){
                                        contentView.isEnabled = false
                                    }
                                }
                            })
                    }
                }else{
                    if(enableView){
                        contentView.isEnabled = false
                    }
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
            translationDistance: Float = DEFAULT_TRANSLATION_VALUE
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
            translationDistance: Float = DEFAULT_TRANSLATION_VALUE
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