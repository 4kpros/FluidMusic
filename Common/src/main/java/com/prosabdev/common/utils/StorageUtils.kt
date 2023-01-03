package com.prosabdev.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

abstract class StorageUtils {

    companion object{
        private const val TAG : String = "StorageUtils"
        private const val AUTHORITY : String = "com.prosabdev.fluidmusic.fileprovider"

        suspend fun saveScreeShotImageBitmap(ctx : Context?, bitmap : Bitmap?) : Uri? {
            if(ctx == null || bitmap == null)
                return null

            var uriResult : Uri? = null

            withContext(Dispatchers.IO){
                val imagesFolder = File(ctx.cacheDir, "images/screenshots")
                try {
                    imagesFolder.mkdirs()
                    val file = File(imagesFolder, "shared_image.png")
                    val stream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    stream.flush()
                    stream.close()
                    uriResult = FileProvider.getUriForFile(ctx, AUTHORITY, file)
                } catch (e: IOException) {
                    Log.d(TAG, "IOException while trying to write file for sharing: " + e.message)
                }
            }
            return uriResult
        }

        fun getScreenShotImageUri(ctx : Context?): Uri? {
            if(ctx == null)
                return null

            val imagesFolder = File(ctx.cacheDir, "images")
            val file = File(imagesFolder, "shared_image.png")
            return FileProvider.getUriForFile(ctx, AUTHORITY, file)
        }
    }
}