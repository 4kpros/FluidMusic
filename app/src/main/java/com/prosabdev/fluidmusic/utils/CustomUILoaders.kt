package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.widget.TextViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prosabdev.fluidmusic.R
import jp.wasabeef.blurry.Blurry


abstract class CustomUILoaders {
    companion object{
        fun loadCovertArtFromBinaryData(context : Context, imageView : ImageView?, bitmap: Bitmap?, widthHeight: Int) {
            loadWithImageLoader(
                context,
                imageView,
                bitmap,
                widthHeight
            )
        }
        fun loadBlurredWithImageLoader(context : Context, imageView : ImageView?, bitmap: Bitmap?, widthHeight: Int) {
            if(bitmap == null || bitmap == null){
                loadWithImageLoader(
                    context,
                    imageView,
                    bitmap,
                    widthHeight,
                    true
                )
                return
            }
            val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    Blurry.with(context)
                        .radius(30)
                        .sampling(1)
                        .from(resource)
                        .into(imageView)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            }
            Glide.with(context)
                .asBitmap()
                .load(bitmap)
                .useAnimationPool(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .useAnimationPool(true)
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(customTarget)
        }
        private fun loadWithImageLoader(context : Context, imageView : ImageView?, bitmap: Bitmap?, widthHeight: Int, transparent : Boolean = false){
            if(imageView == null)
                return
            if(bitmap == null || bitmap == null){
                if(transparent){
                    loadWithImageLoader(
                        context,
                        imageView,
                        android.R.color.transparent,
                        widthHeight
                    )
                }else{
                    loadWithImageLoader(
                        context,
                        imageView,
                        R.drawable.ic_fluid_music_icon_with_padding,
                        widthHeight
                    )
                }
                return
            }
            Glide.with(context)
                .load(bitmap)
                .useAnimationPool(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .useAnimationPool(true)
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
        private fun loadWithImageLoader(context : Context, imageView : ImageView?, resourceId: Int?, widthHeight: Int){
            if(imageView == null)
                return
            Glide.with(context)
                .load(resourceId)
                .useAnimationPool(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .useAnimationPool(true)
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
    }
}