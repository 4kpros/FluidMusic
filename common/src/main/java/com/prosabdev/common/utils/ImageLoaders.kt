package com.prosabdev.common.utils

import android.content.Context
import android.graphics.Bitmap
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
import com.prosabdev.common.R
import com.prosabdev.common.constants.MainConst
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentSkipListMap


object ImageLoaders {
    class ImageRequestItem{
        var imageView: ImageView? = null
        var uri: Uri? = null
        var hashedCovertArtSignature: Int = -1
        var isBlurred: Boolean = false
        var blurRadius: Int = 0
        var blurSample: Int = 0
        var widthHeight: Int = 50
        var animate: Boolean = true
        var animationDuration: Int = 300

        companion object {
            @JvmStatic
            fun newOriginalLargeCardInstance() =
                ImageRequestItem().apply {
                    widthHeight = 1200
                    animate = true
                    animationDuration = 300
                }

            @JvmStatic
            fun newOriginalMediumCardInstance() =
                ImageRequestItem().apply {
                    widthHeight = 500
                    animate = true
                    animationDuration = 300
                }

            @JvmStatic
            fun newOriginalCardInstance() =
                ImageRequestItem().apply {
                    widthHeight = 200
                    animate = true
                    animationDuration = 300
                }

            @JvmStatic
            fun newBlurInstance() =
                ImageRequestItem().apply {
                    widthHeight = 50
                    isBlurred = true
                    blurRadius = 10
                    blurSample = 2
                    animate = true
                    animationDuration = 200
                }
        }
    }

    private val mCachedHashmapImage: ConcurrentSkipListMap<Int, Bitmap> = ConcurrentSkipListMap() //Images below 500px
    private val mCachedHashmapLargeImage: ConcurrentSkipListMap<Int, Bitmap> = ConcurrentSkipListMap() //Images from 500px and more
    private val mCachedHashmapBlurImage: ConcurrentSkipListMap<Int, Bitmap> = ConcurrentSkipListMap() //Images blurred(probably under 150px)

    private val mImageLoaderRequests: ArrayBlockingQueue<ImageRequestItem> = ArrayBlockingQueue(100)
    private var mImageLoaderJobs : ArrayBlockingQueue<Job?> = ArrayBlockingQueue(5)

    private var mCanLoadImages : Boolean = false

    fun initializeJobs(ctx : Context){
        stopAllJobs(ctx, true)
    }
    fun pauseJobs(){
        mCanLoadImages = false
    }
    fun resumeJobs(){
        mCanLoadImages = true
    }
    fun stopAllJobs(ctx : Context, canLoadAtEnd: Boolean = false){
        CoroutineScope(Dispatchers.IO).launch{
            mCanLoadImages = false
            mImageLoaderRequests.clear()
            for (i in mImageLoaderRequests.indices){
                clearImageView(ctx, mImageLoaderRequests.poll()?.imageView)
            }
            mImageLoaderRequests.clear()
            for (i in mImageLoaderJobs.indices){
                mImageLoaderJobs.poll()?.cancel()
            }
            mCanLoadImages = canLoadAtEnd
        }
    }
    fun clearImageView(ctx : Context, imageView: ImageView?){
        if(imageView == null) return
        imageView.setImageBitmap(null)
        Glide.with(ctx).clear(imageView)
    }

    private suspend fun loadImageFromCachedHashMap(ctx: Context, imageRequest: ImageRequestItem?): Boolean {
        return withContext(Dispatchers.IO){
            if(imageRequest == null) return@withContext false
            if(imageRequest.hashedCovertArtSignature <= 0) return@withContext false
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
                if(imageRequest.widthHeight >= 500){
                    if(mCachedHashmapLargeImage.contains(imageRequest.hashedCovertArtSignature)){
                        if(imageRequest.isBlurred){
                            applyBlurry(ctx, imageRequest, mCachedHashmapLargeImage[imageRequest.hashedCovertArtSignature])
                        }else{
                            applyBitmapImage(ctx, imageRequest, mCachedHashmapLargeImage[imageRequest.hashedCovertArtSignature])
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
            }

            return@withContext false
        }
    }

    private fun saveImageToCachedHashMap(imageRequest: ImageRequestItem?, bitmap: Bitmap?) {
        if(imageRequest == null || bitmap == null ) return
        if(imageRequest.hashedCovertArtSignature <= 0) return
        insert(imageRequest, bitmap)
    }
    fun insert(imageRequest: ImageRequestItem?, bitmap: Bitmap?){
        if (bitmap == null || imageRequest == null) {
            return
        }
        if(imageRequest.isBlurred){
            mCachedHashmapBlurImage[imageRequest.hashedCovertArtSignature] = bitmap
            if(mCachedHashmapBlurImage.size >= 10)
                mCachedHashmapBlurImage.pollFirstEntry()
            Log.i(MainConst.TAG, "HASHED BINARY AND SAVED ON BLUR -----> ${mCachedHashmapBlurImage.size}")
        }else{

            if(imageRequest.widthHeight >= 500){
                mCachedHashmapLargeImage[imageRequest.hashedCovertArtSignature] = bitmap
                if(mCachedHashmapLargeImage.size >= 5)
                    mCachedHashmapLargeImage.pollFirstEntry()
                Log.i(MainConst.TAG, "HASHED BINARY AND SAVED ON LARGE ORIGINAL -----> ${mCachedHashmapLargeImage.size}")
            }else{
                mCachedHashmapImage[imageRequest.hashedCovertArtSignature] = bitmap
                if(mCachedHashmapImage.size >= 100)
                    mCachedHashmapImage.pollFirstEntry()
                Log.i(MainConst.TAG, "HASHED BINARY AND SAVED ON ORIGINAL -----> ${mCachedHashmapImage.size}")
            }
        }
    }

    private fun applyBlurry(ctx: Context, imageRequest: ImageRequestItem, bitmap: Bitmap?) {
        if(bitmap == null) return
        imageRequest.imageView?.let { imgView ->
            MainScope().launch {
                Blurry.with(ctx.applicationContext)
                    .radius(imageRequest.blurRadius)
                    .sampling(imageRequest.blurSample)
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
                val byteArray: ByteArray = AudioInfoExtractor.extractImageBinaryDataFromAudioUri(
                    ctx,
                    imgReq.uri
                )
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
                        .centerCrop()
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
        if(imageRequest.hashedCovertArtSignature <= 0) {
            if(imageRequest.isBlurred){
                imageRequest.imageView?.setImageDrawable(null)
            }else{
                loadWithPlaceholderResourceID(ctx, imageRequest.imageView, R.drawable.ic_fluid_music_icon_with_padding)
            }
            return
        }
        if(mImageLoaderRequests.remainingCapacity() <= 5)
        {
            try{
                mImageLoaderRequests.remove()
            }catch (error: Throwable){
                error.printStackTrace()
            }
        }
        try{
            mImageLoaderRequests.add(imageRequest)
        }catch (error: Throwable){
            error.printStackTrace()
        }
        if(mImageLoaderJobs.remainingCapacity() > 0){
            try {
                mImageLoaderJobs.add(
                    CoroutineScope(Dispatchers.Default).launch {
                        var tempCounter: Int = 0
                        while (mImageLoaderRequests.size > 0 && mCanLoadImages){
                            tempCounter++
                            val tempImageRequest : ImageRequestItem? = mImageLoaderRequests.poll()
                            extractBitmapImageAndSaveOnCachingHashmap(ctx, tempImageRequest)
                        }
                        launch {
                            for (i in mImageLoaderJobs.indices){
                                if(mImageLoaderJobs.elementAtOrNull(i) != null && mImageLoaderJobs.elementAtOrNull(i)?.isActive == false)
                                    mImageLoaderJobs.remove(mImageLoaderJobs.elementAtOrNull(i))
                            }
                        }
                    }
                )
            }catch (error: Throwable){
                error.printStackTrace()
            }
        }
    }

    private fun loadWithResourceID(
        context : Context,
        imageView : ImageView?,
        resourceId: Int = 0
    ){
        if(imageView == null)
            return

        MainScope().launch {
            imageView.setImageBitmap(null)
            Glide.with(context.applicationContext)
                .load(resourceId)
                .into(imageView)
        }
    }
    fun loadWithPlaceholderResourceID(
        context : Context,
        imageView : ImageView?,
        resourceId: Int = 0
    ){
        if(imageView == null)
            return

        MainScope().launch {
            Glide.with(context.applicationContext)
                .load(resourceId)
                .placeholder(R.drawable.ic_fluid_music_icon_with_padding)
                .into(imageView)
        }
    }
}