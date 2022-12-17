package com.prosabdev.fluidmusic.models.generic

import android.net.Uri

class GenericItemListGrid {
    var name: String? = ""
    var title: String? = ""
    var subtitle: String? = ""
    var details: String? = ""
    var imageUri: Uri = Uri.EMPTY
    var imageHashedSignature: Int = -1

    companion object {
        const val TAG = "GenericItemListGrid"
    }
}