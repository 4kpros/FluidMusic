package com.prosabdev.common.library

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.prosabdev.common.extensions.*
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.roomdatabase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalSource(
    private val ctx: Context,
    private val source: String,
    private val sourceColumnValue: String,
    private val sourceColumnIndex: String,
    private val sortBy: String,
    private val isInverted: String,
) : MusicSource() {

    private var queueMusic: List<MediaMetadataCompat> = emptyList()

    init {
        state = STATE_INITIALIZING
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = queueMusic.iterator()

    override suspend fun load() {
        updateQueueMusic(
            source,
            sourceColumnValue,
            sourceColumnIndex,
            sortBy,
            isInverted,
        )?.let { updatedQueueMusic ->
            queueMusic = updatedQueueMusic
            state = STATE_INITIALIZED
        } ?: run {
            queueMusic = emptyList()
            state = STATE_ERROR
        }
    }

    private suspend fun updateQueueMusic(
        source: String,
        sourceColumnValue: String,
        sourceColumnIndex: String,
        sortBy: String,
        isInverted: String
    ): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val songList: List<SongItem>? = AppDatabase.getDatabase(ctx).songItemDao().getAllDirectly("")
            val mediaItemList = songList?.mapIndexed { index, songItem ->
                MediaMetadataCompat.Builder()
                    .from(songItem, index.toLong(), songList.size.toLong())
                    .apply {}
                    .build()
            }?.toList()
            // Add description keys to be used by the ExoPlayer MediaSession extension when
            // announcing metadata changes (title, subtitle, description and display icon uri).
            mediaItemList?.forEach { it.description.extras?.putAll(it.bundle) }
            mediaItemList
        }
    }


    /**
     * Extension method for [MediaMetadataCompat.Builder] to set the fields from
     * our [SongItem] constructed object (to make the code a bit easier to see).
     */
    fun MediaMetadataCompat.Builder.from(paramSongItem: SongItem, paramTrackNumber: Long, paramTotalTracksCount: Long): MediaMetadataCompat.Builder {
        id = paramSongItem.id.toString()
        title = paramSongItem.title
        artist = paramSongItem.artist
        album = paramSongItem.album
        duration = paramSongItem.duration
        genre = paramSongItem.genre
        mediaUri = paramSongItem.uri
        albumArtUri = null
        trackNumber = paramTrackNumber
        trackCount = paramTotalTracksCount
        flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

        // To make things easier for *displaying* these, set the display properties as well.
        displayTitle = paramSongItem.title
        displaySubtitle = paramSongItem.artist
        displayDescription = paramSongItem.album
        displayIconUri = null

        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
        // MediaMetadataCompat object. This is needed to send accurate metadata to the
        // media session during updates.
        downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED

        // Allow it to be used in the typical builder style.
        return this
    }
}