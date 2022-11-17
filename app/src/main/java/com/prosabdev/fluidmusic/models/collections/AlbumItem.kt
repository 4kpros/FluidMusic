package com.prosabdev.fluidmusic.models.collections

import org.jaudiotagger.tag.images.Artwork

class AlbumItem {
    var id : Long = 0
    var albumArtist: String? = null
    var album: String? = null
    var artist: String? = null
    var countSongs: Int = 0
    var totalDuration: Long = 0
    //
    var covertArt: Artwork? = null
    var covertArtUrl: String? = null
}