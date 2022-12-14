package com.prosabdev.fluidmusic.models.playlist

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.models.songitem.SongItem

@Entity(
    foreignKeys = [
        ForeignKey(entity = PlaylistItem::class, parentColumns = ["id"], childColumns = ["playlistId"]),
        ForeignKey(entity = SongItem::class, parentColumns = ["uri"], childColumns = ["songUri"], onDelete = ForeignKey.NO_ACTION, onUpdate = ForeignKey.NO_ACTION)
    ]
)
class PlaylistSongItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var playlistId: Long = 0
    var songUri: String = ""
    var lastAddedDateToLibrary: Long = 0

    companion object {
        const val TAG = "PlaylistSongItem"

        val diffCallback = object : DiffUtil.ItemCallback<PlaylistSongItem>() {
            override fun areItemsTheSame(oldItem: PlaylistSongItem, newItem: PlaylistSongItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PlaylistSongItem, newItem: PlaylistSongItem): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.playlistId == newItem.playlistId &&
                        oldItem.songUri == newItem.songUri &&
                        oldItem.lastAddedDateToLibrary == newItem.lastAddedDateToLibrary
            }
        }
    }
}