package com.prosabdev.common.models

data class PlaySongAtRequest (
    var position: Int = -1,
    var playDirectly: Boolean = true,
    var playProgress: Int = 0,
    var repeat: Int? = -1,
    var shuffle: Int? = -1
){
    companion object {
        const val TAG = "PlaySongAtRequest"
    }
}