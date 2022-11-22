package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.prosabdev.fluidmusic.R
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


abstract class CustomUILoaders {
    companion object{

        suspend fun loadCovertArtFromSongUri(
            context : Context,
            imageView : ImageView?,
            uri: Uri?,
            widthHeight: Int? = null
        ) = coroutineScope {
            if(imageView == null)
                return@coroutineScope

            if (uri == null || uri.toString().isEmpty()) {
                loadWithResourceID(context, imageView, null)
                return@coroutineScope
            }

            val byteArray: ByteArray? =
                CustomAudioInfoExtractor.extractImageBinaryDataFromAudioUri(context, uri)

            if (byteArray == null) {
                loadWithResourceID(context, imageView, null)
                return@coroutineScope
            }

            if(widthHeight != null){
                MainScope().launch {
                    Glide.with(context)
                        .load(byteArray)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .apply(RequestOptions().override(widthHeight, widthHeight))
                        .into(imageView)
                }
            }else{
                MainScope().launch {
                    Glide.with(context)
                        .load(byteArray)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(imageView)
                }
            }
        }
        suspend fun loadBlurredCovertArtFromSongUri(
            context : Context,
            imageView : ImageView?,
            uri: Uri?,
            widthHeight: Int? = null
        ) = coroutineScope {
            if(imageView == null)
                return@coroutineScope
            if (uri == null || uri.toString().isEmpty())
                return@coroutineScope

            val byteArray: ByteArray? =
                CustomAudioInfoExtractor.extractImageBinaryDataFromAudioUri(context, uri)

            if (byteArray == null) {
                Glide.with(context).clear(imageView)
                MainScope().launch {
                    imageView.setImageBitmap(null)
                }
                return@coroutineScope
            }
            val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    Blurry.with(context)
                        .radius(25)
                        .sampling(2)
                        .from(resource)
                        .into(imageView)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            }
            val factory =
                DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            MainScope().launch {
                if(widthHeight != null){
                    Glide.with(context)
                        .asBitmap()
                        .load(byteArray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(ObjectKey(uri.toString()))
                        .override(widthHeight, widthHeight)
                        .transition(withCrossFade(factory))
                        .into(customTarget)
                }else{
                    Glide.with(context)
                        .asBitmap()
                        .load(byteArray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(ObjectKey(uri.toString()))
                        .transition(withCrossFade(factory))
                        .into(customTarget)
                }
            }
        }

        fun loadWithResourceID(
            context : Context,
            imageView : ImageView?,
            resourceId: Int?
        ){
            if(imageView == null)
                return

            MainScope().launch {
                Glide.with(context)
                    .load(resourceId)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                    .into(imageView)
            }
        }
    }
}