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


abstract class ImageLoadersUtils {
    companion object{

        suspend fun loadCovertArtFromSongUri(
            context : Context,
            imageView : ImageView?,
            uri: Uri?,
            widthHeight: Int? = null,
            crossFadeDuration : Int = 0,
            showPlaceHolder : Boolean = false
        ) = coroutineScope {
            if(imageView == null)
                return@coroutineScope

            if (uri == null || uri.toString().isEmpty()) {
                loadWithResourceID(context, imageView, 0, crossFadeDuration)
                return@coroutineScope
            }

            val byteArray: ByteArray? =
                AudioInfoExtractorUtils.extractImageBinaryDataFromAudioUri(context, uri)

            if (byteArray == null) {
                loadWithResourceID(context, imageView, 0, crossFadeDuration)
                return@coroutineScope
            }

            if(widthHeight != null){
                MainScope().launch {
                    Glide.with(context)
                        .load(byteArray)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
//                        .signature(ObjectKey(byteArray.decodeToString()))
                        .transition(DrawableTransitionOptions.withCrossFade(crossFadeDuration))
                        .placeholder(
                            if(showPlaceHolder)
                                R.drawable.ic_fluid_music_icon_with_padding
                            else
                                R.color.transparent
                        )
                        .apply(RequestOptions().override(widthHeight, widthHeight))
                        .into(imageView)
                }
            }else{
                MainScope().launch {
                    Glide.with(context)
                        .load(byteArray)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
//                        .signature(ObjectKey(byteArray.decodeToString()))
                        .transition(DrawableTransitionOptions.withCrossFade(crossFadeDuration))
                        .placeholder(
                            if(showPlaceHolder)
                                R.drawable.ic_fluid_music_icon_with_padding
                            else
                                R.color.transparent
                        )
                        .into(imageView)
                }
            }
        }

        suspend fun loadBlurredCovertArtFromSongUri(
            context : Context,
            imageView : ImageView?,
            uri: Uri?,
            widthHeight: Int = 100
        ) = coroutineScope {
            if (imageView == null)
                return@coroutineScope
            if (uri == null || uri.toString().isEmpty())
                return@coroutineScope

            val byteArray: ByteArray? =
                AudioInfoExtractorUtils.extractImageBinaryDataFromAudioUri(context, uri)

            if (byteArray == null) {
                MainScope().launch {
                    imageView.setImageBitmap(null)
                    Glide.with(context).clear(imageView)
                }
                return@coroutineScope
            }
            val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    MainScope().launch {
                        Blurry.with(context)
                            .radius(15)
                            .sampling(2)
                            .from(resource)
                            .into(imageView)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            }
            val factory =
                DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            MainScope().launch {
                Glide.with(context)
                    .asBitmap()
                    .load(byteArray)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .signature(ObjectKey(byteArray.decodeToString()))
                    .override(widthHeight, widthHeight)
                    .transition(withCrossFade(factory))
                    .into(customTarget)
            }

        }

        fun loadWithResourceID(
            context : Context,
            imageView : ImageView?,
            resourceId: Int = 0,
            crossFadeDuration : Int = 0
        ){
            if(imageView == null)
                return

            MainScope().launch {
                Glide.with(context)
                    .load(resourceId)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .signature(ObjectKey(resourceId))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(crossFadeDuration))
                    .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                    .into(imageView)
            }
        }
    }
}