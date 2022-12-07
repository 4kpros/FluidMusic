package com.prosabdev.fluidmusic.models.generic

import android.net.Uri

class GenericItemListGrid {
    var title: String? = null
    var subtitle: String? = null
    var details: String? = null
    var imageUri: Uri? = Uri.EMPTY
    var imageHashedSignature: Int = -1

    companion object {
        const val TAG = "GenericItemListGrid"
    }
}