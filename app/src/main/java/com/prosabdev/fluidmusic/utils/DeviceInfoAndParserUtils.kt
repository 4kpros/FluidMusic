package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.prosabdev.fluidmusic.models.FolderUriTree


abstract class DeviceInfoAndParserUtils {
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