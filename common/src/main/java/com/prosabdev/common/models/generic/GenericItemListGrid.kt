package com.prosabdev.common.models.generic

import android.net.Uri

data class GenericItemListGrid (
    var name: String? = "",
    var title: String? = "",
    var subtitle: String? = "",
    var details: String? = "",
    var mediaUri: Uri = Uri.EMPTY,
    var hashedCovertArtSignature: Int = -1
){
    companion object {
        const val TAG = "GenericItemListGrid"
    }
}