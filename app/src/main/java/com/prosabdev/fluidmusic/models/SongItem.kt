package com.prosabdev.fluidmusic.models

import android.graphics.Bitmap
import org.jaudiotagger.tag.images.Artwork

class SongItem() {
    var id : Long = 0
    var fileName: String? = null
    var title: String? = null
    var artist: String? = null
    var albumArtist: String? = null
    var album: String? = null
    var genre: String? = null
    var relativePath: String? = null
    var absolutePath: String? = null
    var folder: String? = null
    var year: String? = null
    var duration: Long = 0

    //
    var typeMime: String? = null
    var sampleRate = 0
    var bitrate = 0.0

    //
    var lastUpdateDate: String? = null
    var lastAddedDate: String? = null

    //
    var storageName: String? = null

    //
    var covertArt: Artwork? = null
}