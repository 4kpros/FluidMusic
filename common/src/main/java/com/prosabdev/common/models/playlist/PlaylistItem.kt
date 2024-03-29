package com.prosabdev.common.models.playlist

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["name"], unique = true)]
)
data class PlaylistItem (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String? = "",
    var lastUpdateDate: Long = 0,
    var lastAddedDateToLibrary: Long = 0,
    var isRealFile: Boolean = false,
    var uri: String? = ""
){
    companion object {
        const val TAG = "PlaylistItem"
        const val INDEX_COLUM_TO_SONG_ITEM = "playlistId"

        fun getStringIndexForSelection(dataItem: Any?): String {
            if(dataItem != null && dataItem is PlaylistItem) {
                return dataItem.name ?: ""
            }
            return ""
        }
        fun getStringIndexForFastScroller(dataItem: Any): String {
            if(dataItem is PlaylistItem) {
                return dataItem.name ?: ""
            }
            return "#"
        }

        val diffCallback = object : DiffUtil.ItemCallback<PlaylistItem>() {
            override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
                return oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.lastUpdateDate == newItem.lastUpdateDate &&
                    oldItem.lastAddedDateToLibrary == newItem.lastAddedDateToLibrary &&
                    oldItem.isRealFile == newItem.isRealFile &&
                    oldItem.uri == newItem.uri

            }
        }
    }
}