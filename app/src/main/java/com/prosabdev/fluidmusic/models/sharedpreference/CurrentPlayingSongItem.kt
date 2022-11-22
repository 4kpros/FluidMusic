package com.prosabdev.fluidmusic.models.sharedpreference

class CurrentPlayingSongItem {
    var position : Long = -1
    var uriTreeId: Long = -1
    var id : Long = -1
    var uri : String? = null
    var fileName: String? = null
    var title: String? = null
    var artist: String? = null
    var duration: Long = 0
    var currentSeekDuration: Long = 0
    var typeMime: String? = null
}