package com.prosabdev.fluidmusic.models.sharedpreference

class CurrentPlayingSongItem {
    var id : Long = -1
    var uriTreeId: Long = -1
    var uri : String? = null
    var fileName: String? = null
    var title: String? = null
    var artist: String? = null
    var duration: Long = 0
    var typeMime: String? = null
}