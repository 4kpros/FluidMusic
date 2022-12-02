package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentSkipListMap


abstract class ImageLoadersUtils {

    class ImageRequestItem{
        var imageView: ImageView? = null
        var uri: Uri? = null
        var hashedCovertArtSignature: Int = -1
        var isBlurred: Boolean = false
        var widthHeight: Int = 500
        var animate: Boolean = true
        var animationDuration: Int = 200

        companion object {

            @JvmStatic
            fun newOriginalCardInstance() =
                ImageRequestItem().apply {
                    widthHeight = 200
                    isBlurred = false
                    animate = true
                    animationDuration = 200
                }

            @JvmStatic
            fun newBlurInstance() =
                ImageRequestItem().apply {
                    widthHeight = 100
                    isBlurred = true
                    animate = true
                    animationDuration = 200
                }
        }
    }

    companion object{
        private val mCachedHashmapImage: ConcurrentSkipListMap<Int, Bitmap> = ConcurrentSkipListMap()
        private val mCachedHashmapBlurImage: ConcurrentSkipListMap<Int, Bitmap> = ConcurrentSkipListMap()

        private val mImageLoaderRequests: ArrayBlockingQueue<ImageRequestItem> = ArrayBlockingQueue(100)

        private var mImageLoaderJobs : ArrayBlockingQueue<Job?> = ArrayBlockingQueue(10)

        private var mCanLoadImages : Boolean = false

        fun initializeJobWorker(){
            mCanLoadImages = true
        }
        fun stopAllJobsWorkers(){
            mCanLoadImages = false
            for (i in mImageLoaderJobs.indices){
                mImageLoaderJobs.elementAt(i)?.cancel()
            }
            mImageLoaderRequests.clear()
        }

        private suspend fun loadImageFromCachedHashMap(ctx: Context, imageRequest: ImageRequestItem?): Boolean {
            return withContext(Dispatchers.IO){
                if(imageRequest == null) return@withContext false
                if(imageRequest.hashedCovertArtSignature == -1) return@withContext false
                if(imageRequest.isBlurred){
                    if(mCachedHashmapBlurImage.contains(imageRequest.hashedCovertArtSignature)){
                        if(imageRequest.isBlurred){
                            applyBlurry(ctx, imageRequest, mCachedHashmapBlurImage[imageRequest.hashedCovertArtSignature])
                        }else{
                            applyBitmapImage(ctx, imageRequest, mCachedHashmapBlurImage[imageRequest.hashedCovertArtSignature])
                        }
                        return@withContext true
                    }
                }else{
                    if(mCachedHashmapImage.contains(imageRequest.hashedCovertArtSignature)){
                        if(imageRequest.isBlurred){
                            applyBlurry(ctx, imageRequest, mCachedHashmapImage[imageRequest.hashedCovertArtSignature])
                        }else{
                            applyBitmapImage(ctx, imageRequest, mCachedHashmapImage[imageRequest.hashedCovertArtSignature])
                        }
                        return@withContext true
                    }
                }

                return@withContext false
            }
        }

        private fun saveImageToCachedHashMap(imageRequest: ImageRequestItem?, bitmap: Bitmap?) {
            if(imageRequest == null || bitmap == null ) return
            if(imageRequest.hashedCovertArtSignature == -1) return
            insert(imageRequest, bitmap)
        }
        fun insert(imageRequest: ImageRequestItem?, bitmap: Bitmap?){
            if (bitmap == null || imageRequest == null) {
                return
            }
            if(imageRequest.isBlurred){
                mCachedHashmapBlurImage
                mCachedHashmapBlurImage[imageRequest.hashedCovertArtSignature] = bitmap
                if(mCachedHashmapBlurImage.size >= 10)
                    mCachedHashmapBlurImage.pollFirstEntry()
                Log.i(ConstantValues.TAG, "HASHED BINARY AND SAVED ON BLUR -----> ${mCachedHashmapBlurImage.size}")
            }else{

                mCachedHashmapImage[imageRequest.hashedCovertArtSignature] = bitmap
                if(mCachedHashmapImage.size >= 150)
                    mCachedHashmapImage.pollFirstEntry()
                Log.i(ConstantValues.TAG, "HASHED BINARY AND SAVED ON ORIGINAL -----> ${mCachedHashmapImage.size}")
            }
        }

        private fun applyBlurry(ctx: Context, imageRequest: ImageRequestItem, bitmap: Bitmap?) {
            if(bitmap == null) return
            imageRequest.imageView?.let { imgView ->
                MainScope().launch {
                    Blurry.with(ctx.applicationContext)
                        .radius(15)
                        .sampling(4)
                        .animate(
                            if(imageRequest.animate) imageRequest.animationDuration else 0
                        )
                        .from(bitmap)
                        .into(imgView)
                }
            }
        }

        private fun applyBitmapImage(ctx: Context, imageRequest: ImageRequestItem, bitmap: Bitmap?) {
            if(bitmap == null) return
            imageRequest.imageView?.let { imgView ->
                MainScope().launch {
                    Glide.with(ctx.applicationContext)
                        .load(bitmap)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transition(DrawableTransitionOptions.withCrossFade(
                            if(imageRequest.animate) imageRequest.animationDuration else 0
                        ))
                        .placeholder(
                            if(imageRequest.animate)
                                R.drawable.ic_fluid_music_icon_with_padding
                            else
                                -1
                        )
                        .apply(RequestOptions().override(imageRequest.widthHeight, imageRequest.widthHeight))
                        .into(imgView)
                }
            }
        }

        private suspend fun extractBitmapImageAndSaveOnCachingHashmap(ctx: Context, imageRequest: ImageRequestItem?) {
            val isCached : Boolean = loadImageFromCachedHashMap(ctx, imageRequest)
            if(!isCached){
                imageRequest?.let imageRequestLet@ { imgReq ->
                    val byteArray: ByteArray = AudioInfoExtractorUtils.extractImageBinaryDataFromAudioUri(ctx, imgReq.uri)
                        ?: return@imageRequestLet

                    val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            if(imgReq.isBlurred){
                                applyBlurry(ctx, imgReq, resource)
                            }else{
                                applyBitmapImage(ctx, imgReq, resource)
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                saveImageToCachedHashMap(imgReq, resource)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    }
                    val factory =
                        DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                    MainScope().launch {
                        Glide.with(ctx.applicationContext)
                            .asBitmap()
                            .load(byteArray)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(imgReq.widthHeight, imgReq.widthHeight)
                            .transition(withCrossFade(factory))
                            .into(customTarget)
                    }
                }
            }
        }

        fun startExploreContentImageLoaderJob(ctx: Context, imageRequest: ImageRequestItem?) {
            if(imageRequest == null) return
            if(imageRequest.imageView == null) return
            if(imageRequest.hashedCovertArtSignature == -1) return
            if(mImageLoaderJobs.remainingCapacity() == 0)
                mImageLoaderJobs.poll()
            mImageLoaderRequests.add(imageRequest)
            mImageLoaderJobs.add(
                CoroutineScope(Dispatchers.Default).launch {
                    var tempCounter: Int = 0
                    while (mImageLoaderRequests.size > 0 && mCanLoadImages){
                        tempCounter++
                        val tempImageRequest : ImageRequestItem? = mImageLoaderRequests.poll()
                        extractBitmapImageAndSaveOnCachingHashmap(ctx, tempImageRequest)
                    }
                    for (i in mImageLoaderJobs.indices){
                        if(mImageLoaderJobs.elementAtOrNull(i) != null && mImageLoaderJobs.elementAtOrNull(i)?.isActive == false)
                            mImageLoaderJobs.remove(mImageLoaderJobs.elementAtOrNull(i))
                    }
                }
            )
        }

        fun startLoadCachedImageFromSignature(ctx: Context, imageRequest: ImageRequestItem?) {
            if(imageRequest == null) return
            if(imageRequest.imageView == null) return
            if(imageRequest.hashedCovertArtSignature == -1) return
            MainScope().launch {
                loadImageFromCachedHashMap(ctx, imageRequest)
            }
        }

        suspend fun loadCovertArtFromSongUri(
            context : Context,
            imageView : ImageView?,
            uri: Uri?,
            hashedCovertArtSignature: Int,
            widthHeight: Int = 500,
            animate: Boolean = true
        ) = withContext(Dispatchers.IO) {
            if(imageView == null)
                return@withContext

            if (uri == null || uri.toString().isEmpty() || hashedCovertArtSignature == -1) {
                loadWithResourceID(context, imageView, 0)
                return@withContext
            }

            val byteArray: ByteArray? = AudioInfoExtractorUtils.extractImageBinaryDataFromAudioUri(context, uri)

            if (byteArray == null) {
                loadWithResourceID(context, imageView, 0)
                return@withContext
            }

            MainScope().launch {
                Glide.with(context.applicationContext)
                    .load(byteArray)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transition(DrawableTransitionOptions.withCrossFade(
                        if(animate) 250 else 0
                    ))
                    .placeholder(
                        if(animate)
                            R.drawable.ic_fluid_music_icon_with_padding
                        else
                            -1
                    )
                    .apply(RequestOptions().override(widthHeight, widthHeight))
                    .into(imageView)
            }
        }

        suspend fun loadBlurredCovertArtFromSongUri(
            context : Context,
            imageView : ImageView?,
            uri: Uri?,
            hashedCovertArtSignature: Int,
            widthHeight: Int = 50
        ) = withContext(Dispatchers.IO) {
            if (imageView == null)
                return@withContext

            if (uri == null || uri.toString().isEmpty() || hashedCovertArtSignature == -1){
                MainScope().launch {
                    imageView.setImageBitmap(null)
                    Glide.with(context.applicationContext).clear(imageView)
                }
                return@withContext
            }

            val byteArray: ByteArray? = AudioInfoExtractorUtils.extractImageBinaryDataFromAudioUri(context, uri)

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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(widthHeight, widthHeight)
                    .transition(withCrossFade(factory))
                    .into(customTarget)
            }

        }

        fun loadWithDefaultCoverArt(
            context : Context,
            imageView : ImageView?
        ){
            if(imageView == null)
                return

            MainScope().launch {
                Glide.with(context.applicationContext)
                    .load(R.drawable.ic_fluid_music_icon_with_padding)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                    .into(imageView)
            }
        }

        fun loadWithResourceID(
            context : Context,
            imageView : ImageView?,
            resourceId: Int = 0
        ){
            if(imageView == null)
                return

            MainScope().launch {
                Glide.with(context.applicationContext)
                    .load(resourceId)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                    .into(imageView)
            }
        }

        fun hasImage(view: ImageView): Boolean {
            val drawable: Drawable? = view.drawable
            var hasImage = drawable != null
            if (hasImage && drawable is BitmapDrawable) {
                hasImage = drawable.bitmap != null
            }
            return hasImage
        }
    }
}