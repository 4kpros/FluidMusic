package com.prosabdev.fluidmusic.models.playlist

import androidx.recyclerview.widget.DiffUtil
import androidx.room.DatabaseView

@DatabaseView("SELECT PlaylistItem.id as playlistId, PlaylistItem.name as playlistName, " +
        "MAX(PlaylistSongItem.addedDate) as lastAddedDate, " +
        "COUNT(SongItem.id) as numberTracks, " +
        "SUM(SongItem.duration) as totalDuration " +
        "FROM PlaylistItem " +
        "INNER JOIN PlaylistSongItem ON PlaylistItem.id = PlaylistSongItem.playlistId "+
        "INNER JOIN SongItem ON PlaylistSongItem.songId = SongItem.id " +
        "GROUP BY PlaylistItem.id ORDER BY PlaylistItem.id")
class PlaylistItemView {
    var playlistId: Long = 0
    var playlistName: String = ""
    var numberTracks: Int = 0
    var totalDuration: Long = 0
    var lastAddedDate : Long = 0



    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<PlaylistItemView>() {
            override fun areItemsTheSame(oldItem: PlaylistItemView, newItem: PlaylistItemView): Boolean {
                return oldItem.playlistId == newItem.playlistId
            }

            override fun areContentsTheSame(oldItem: PlaylistItemView, newItem: PlaylistItemView): Boolean {
                return oldItem == newItem
            }
        }
    }
}