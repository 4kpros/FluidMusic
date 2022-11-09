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
        fun loadCovertArtFromBinaryData(context : Context, imageView : ImageView?, binaryData: ByteArray?, widthHeight: Int) {
            loadWithBinaryData(
                context,
                imageView,
                binaryData,
                widthHeight
            )
        }
        fun loadBlurredWithImageLoader(context : Context, imageView : ImageView?, binaryData: ByteArray?, widthHeight: Int = 10) {
            if(binaryData == null || binaryData.isEmpty()){
                loadWithBinaryData(context, imageView, null, widthHeight, true)
                return
            }
            val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    Blurry.with(context)
                        .radius(40)
                        .sampling(1)
                        .from(resource)
                        .into(imageView)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            }
            Glide.with(context)
                .asBitmap()
                .load(binaryData)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(customTarget)
        }
        private fun loadWithBinaryData(context : Context, imageView : ImageView?, binaryData: ByteArray?, widthHeight: Int, transparent : Boolean = false){
            if(imageView == null)
                return
            if(binaryData == null || binaryData.isEmpty()){
                if(transparent){
                    loadWithResourceID(
                        context,
                        imageView,
                        android.R.color.transparent,
                        widthHeight
                    )
                }else{
                    loadWithResourceID(
                        context,
                        imageView,
                        R.drawable.ic_fluid_music_icon_with_padding,
                        widthHeight
                    )
                }
                return
            }
            Glide.with(context)
                .load(binaryData)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .dontAnimate()
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
        fun loadWithResourceID(context : Context, imageView : ImageView?, resourceId: Int?, widthHeight: Int){
            if(imageView == null)
                return
            Glide.with(context)
                .load(resourceId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
    }
}