package com.prosabdev.fluidmusic.models.playlist

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["name"], unique = true)]
)
class PlaylistItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String = ""
    var lastUpdateDate: Long = 0
    var lastAddedDateToLibrary: Long = 0
    var isRealFile: Boolean = false
    var uri: String? = null

    companion object {
        const val TAG = "PlaylistItem"

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