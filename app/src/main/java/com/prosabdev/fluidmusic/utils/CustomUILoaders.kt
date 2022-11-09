package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
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
        fun loadBlurredWithImageLoader(context : Context, imageView : ImageView?, binaryData: ByteArray?, widthHeight: Int = 1) {
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
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(customTarget)
        }
        private fun loadWithBinaryData(context : Context, imageView : ImageView?, binaryData: ByteArray?, widthHeight: Int, transparent : Boolean = false){
            if(imageView == null)
                return
            var placeHolderImage : Int = R.drawable.ic_fluid_music_icon_with_padding
            if(transparent)
                placeHolderImage = android.R.color.transparent
            Glide.with(context)
                .load(binaryData)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .placeholder(placeHolderImage)
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
        fun loadWithResourceID(context : Context, imageView : ImageView?, resourceId: Int?, widthHeight: Int){
            if(imageView == null)
                return
            Glide.with(context)
                .load(0)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
    }
}