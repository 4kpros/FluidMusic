package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.Environment.isExternalStorageRemovable
import androidx.collection.LruCache
import com.bumptech.glide.disklrucache.DiskLruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


abstract class CustomBitmapCacheOptimizations {

    companion object{
        private const val APP_VERSION : Int = 1
        private const val DISK_CACHE_SIZE : Long = 1024 * 1024 * 10 // 10MB
        private const val DISK_CACHE_SUBDIR = "thumbnails"
        private var diskLruCache: DiskLruCache? = null
        private val diskCacheLock = ReentrantLock()
        private val diskCacheLockCondition: Condition = diskCacheLock.newCondition()
        private var diskCacheStarting = true

        private lateinit var memoryCache: LruCache<String, Bitmap>

        suspend fun initBitmapMemoryCachingSystem() {
            withContext(Dispatchers.IO){
                val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
                val cacheSize = maxMemory / 8
                memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
                    override fun sizeOf(key: String, bitmap: Bitmap): Int {
                        return bitmap.byteCount / 1024
                    }
                }
            }
        }

        suspend fun initBitmapDiskCachingSystem(ctx : Context) {
            val cacheDir = getDiskCacheDir(ctx, DISK_CACHE_SUBDIR)
            initDiskCacheTask(cacheDir)
        }
        private suspend fun initDiskCacheTask(vararg cacheDirParams: File) {
            withContext(Dispatchers.IO){
                diskCacheLock.withLock {
                    val cacheDir = cacheDirParams[0]
                    diskLruCache = DiskLruCache.open(cacheDir, APP_VERSION, 1, DISK_CACHE_SIZE)
                    diskCacheStarting = false // Finished initialization
                    diskCacheLockCondition.signalAll() // Wake any waiting threads
                }
            }
        }
        private fun getDiskCacheDir(context: Context, uniqueName: String): File {
            val cachePath =
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                    || !isExternalStorageRemovable()) {
                    context.externalCacheDir?.path
                } else {
                    context.cacheDir.path
                }
            return File(cachePath + File.separator + uniqueName)
        }

        suspend fun addBitmapToCache(key: String, bitmap: Bitmap) {
            withContext(Dispatchers.IO){
                // Add to memory cache as before
                if (getBitmapFromMemoryCache(key) == null) {
                    memoryCache.put(key, bitmap)
                }
//
//                // Also add to disk cache
//                synchronized(diskCacheLock) {
//                    diskLruCache?.apply {
//                        if (this.get(key) == null) {
//                            this.put(key, bitmap)
//                        }
//                    }
//                }
            }
        }

        suspend fun getBitmapFromCache(key: String): Bitmap? {
            return getBitmapFromMemoryCache(key) ?: return getBitmapFromDiskCache(key)
        }
        private fun getBitmapFromMemoryCache(key: String): Bitmap? {
            return memoryCache[key]
        }
        private suspend fun getBitmapFromDiskCache(key: String): Bitmap? = null
//            withContext(Dispatchers.IO){
//                diskCacheLock.withLock {
//                    while (diskCacheStarting) {
//                        try {
//                            diskCacheLockCondition.await()
//                        } catch (e: InterruptedException) {
//                            //
//                        }
//                    }
//                    return@withContext diskLruCache?.get(key) as Bitmap?
//                }
//            }
    }
}