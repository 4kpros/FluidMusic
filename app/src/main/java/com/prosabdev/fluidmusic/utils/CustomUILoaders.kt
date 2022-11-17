package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.prosabdev.fluidmusic.R
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.coroutineScope


abstract class CustomUILoaders {
    companion object{
        suspend fun loadCovertArtFromBinaryData(
            context : Context,
            imageView : ImageView?,
            binaryData: ByteArray?,
            widthHeight: Int
        ) = coroutineScope {
            loadWithBinaryData(
                context,
                imageView,
                binaryData,
                widthHeight
            )
        }
        suspend fun loadBlurredWithImageLoader(
            context : Context,
            imageView : ImageView?,
            binaryData: ByteArray?,
            widthHeight: Int = 10
        ) = coroutineScope {
            if(binaryData == null || binaryData.isEmpty()){
                loadWithBinaryData(context, imageView, null, widthHeight, true)
                return@coroutineScope
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
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            Glide.with(context)
                .asBitmap()
                .load(binaryData)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transition(withCrossFade(factory))
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(customTarget)
        }
        suspend fun loadWithBinaryDataWithCrossFade(
            context : Context,
            imageView : ImageView?,
            binaryData: ByteArray?,
            widthHeight: Int
        ) = coroutineScope {
            if(imageView == null)
                return@coroutineScope
            Glide.with(context)
                .load(binaryData)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                .centerCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
        private suspend fun loadWithBinaryData(
            context : Context,
            imageView : ImageView?,
            binaryData: ByteArray?,
            widthHeight: Int,
            transparent : Boolean = false
        ) = coroutineScope {
            if(imageView == null)
                return@coroutineScope
            var placeHolderImage : Int = R.drawable.ic_fluid_music_icon_with_padding
            if(transparent)
                placeHolderImage = android.R.color.transparent
            Glide.with(context)
                .load(binaryData)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .placeholder(placeHolderImage)
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
        suspend fun loadWithResourceID(
            context : Context,
            imageView : ImageView?,
            resourceId: Int?,
            widthHeight: Int
        ) = coroutineScope {
            if(imageView == null)
                return@coroutineScope
            Glide.with(context)
                .load(0)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(imageView)
        }
    }
}