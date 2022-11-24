package com.prosabdev.fluidmusic.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.provider.DocumentsContractCompat
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.explore.SongItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit


abstract class CustomDeviceInfoAndParser {
    companion object {
        const val STORAGE_ID_PRIMARY = "primary"
        const val STORAGE_ID_DATA = "data"

        private fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        private fun capitalize(s: String?): String {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                first.uppercaseChar().toString() + s.substring(1)
            }
        }

        fun formatAndReturnFolderUriSAF(context: Context, uri: Uri): FolderUriTree {
            val documentFile = DocumentFile.fromTreeUri(context, uri)

            val tempFolderUriTree = FolderUriTree()
            tempFolderUriTree.uriTree = documentFile?.uri.toString()
            tempFolderUriTree.lastPathSegment = uri.lastPathSegment
            tempFolderUriTree.pathTree = uri.path.toString().trim()
            tempFolderUriTree.normalizeScheme = uri.normalizeScheme().toString()
            tempFolderUriTree.path = "/${(uri.lastPathSegment ?: "").substringAfter(":")}"
            tempFolderUriTree.deviceName =
                if ((uri.lastPathSegment ?: "").substringBefore(":") == STORAGE_ID_PRIMARY)
                    getDeviceName()
                else
                    getDeviceName()
            return tempFolderUriTree
        }
    }
}