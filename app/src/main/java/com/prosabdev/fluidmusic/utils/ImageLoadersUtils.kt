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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class ImageLoadersUtils {
    companion object{

        suspend fun loadCovertArtFromSongUri(
            context : Context,
            imageView : ImageView?,
            uri: Uri?,
            widthHeight: Int? = null,
            crossFadeDuration : Int = 0,
            showPlaceHolder : Boolean = false
        ) = withContext(Dispatchers.IO) {
            if(imageView == null)
                return@withContext

            if (uri == null || uri.toString().isEmpty()) {
                loadWithResourceID(context, imageView, 0, crossFadeDuration)
                return@withContext
            }

            val byteArray: ByteArray? =
                AudioInfoExtractorUtils.extractImageBinaryDataFromAudioUri(context, uri)

            if (byteArray == null) {
                loadWithResourceID(context, imageView, 0, crossFadeDuration)
                return@withContext
            }

            if(widthHeight != null){
                MainScope().launch {
                    Glide.with(context.applicationContext)
                        .load(byteArray)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
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
                    Glide.with(context.applicationContext)
                        .load(byteArray)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
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
        ) = withContext(Dispatchers.IO) {
            if (imageView == null)
                return@withContext
            if (uri == null || uri.toString().isEmpty())
                return@withContext

            val byteArray: ByteArray? =
                AudioInfoExtractorUtils.extractImageBinaryDataFromAudioUri(context, uri)

            if (byteArray == null) {
                MainScope().launch {
                    imageView.setImageBitmap(null)
                    Glide.with(context.applicationContext).clear(imageView)
                }
                return@withContext
            }
            val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    MainScope().launch {
                        Blurry.with(context.applicationContext)
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
                Glide.with(context.applicationContext)
                    .asBitmap()
                    .load(byteArray)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                Glide.with(context.applicationContext)
                    .load(resourceId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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