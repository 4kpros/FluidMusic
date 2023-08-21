package com.prosabdev.common.library

import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.annotation.IntDef
import com.prosabdev.common.extensions.*


abstract class MusicSource : IMusicSource {

    @IntDef(
        STATE_CREATED,
        STATE_INITIALIZING,
        STATE_INITIALIZED,
        STATE_ERROR
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class State

    @State
    var state: Int = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    /**
     * Performs an action when this MusicSource is ready.
     *
     * This method is *not* threadsafe. Ensure actions and state changes are only performed
     * on a single thread.
     */
    override fun whenReady(performAction: (Boolean) -> Unit): Boolean =
        when (state) {
            STATE_CREATED, STATE_INITIALIZING -> {
                onReadyListeners += performAction
                false
            }
            else -> {
                performAction(state != STATE_ERROR)
                true
            }
        }

    /**
     * Handles searching a [MusicSource] from a focused voice search, often coming
     * from the Google Assistant.
     */
    override fun search(query: String, extras: Bundle): List<MediaMetadataCompat> {
        // First attempt to search with the "focus" that's provided in the extras.
        val focusSearchResult = when (extras.getString(MediaStore.EXTRA_MEDIA_FOCUS)) {
            MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                // For a Genre focused search, only genre is set.
                val genre = extras.getString(MediaStore.EXTRA_MEDIA_GENRE)
                Log.d(TAG, "Focused genre search: '$genre'")
                filter { song ->
                    song.mediaMetadata == genre
                }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                // For an Artist focused search, only the artist is set.
                val artist = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
                Log.d(TAG, "Focused artist search: '$artist'")
                filter { song ->
                    (song.artist == artist || song.albumArtist == artist)
                }
            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                // For an Album focused search, album and artist are set.
                val artist = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
                val album = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM)
                Log.d(TAG, "Focused album search: album='$album' artist='$artist")
                filter { song ->
                    (song.artist == artist || song.albumArtist == artist) && song.album == album
                }
            }
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                // For a Song (aka Media) focused search, title, album, and artist are set.
                val title = extras.getString(MediaStore.EXTRA_MEDIA_TITLE)
                val album = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM)
                val artist = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
                Log.d(TAG, "Focused media search: title='$title' album='$album' artist='$artist")
                filter { song ->
                    (song.artist == artist || song.albumArtist == artist) && song.album == album
                            && song.title == title
                }
            }
            else -> {
                // There isn't a focus, so no results yet.
                emptyList()
            }
        }

        // If there weren't any results from the focused search (or if there wasn't a focus
        // to begin with), try to find any matches given the 'query' provided, searching against
        // a few of the fields.
        // In this sample, we're just checking a few fields with the provided query, but in a
        // more complex app, more logic could be used to find fuzzy matches, etc...
        if (focusSearchResult.isEmpty()) {
            return if (query.isNotBlank()) {
                Log.d(TAG, "Unfocused search for '$query'")
                filter { song ->
                    song.title.toString().contains(query, true) ||
                            song.artist.toString().contains(query, true) ||
                            song.albumArtist.toString().contains(query, true) ||
                            song.displayTitle.toString().contains(query, true) ||
                            song.displaySubtitle.toString().contains(query, true) ||
                            song.displayDescription.toString().contains(query, true) ||
                            song.album.toString().contains(query, true) ||
                            song.composer.toString().contains(query, true) ||
                            song.genre.toString().contains(query, true) ||
                            song.author.toString().contains(query, true) ||
                            song.writer.toString().contains(query, true) ||
                            song.year.toString().contains(query, true) ||
                            song.date.toString().contains(query, true) ||
                            song.trackNumber.toString().contains(query, true) ||
                            song.discNumber.toString().contains(query, true)
                }
            } else {
                // If the user asked to "play music", or something similar, the query will also
                // be blank. Given the small catalog of songs in the sample, just return them
                // all, shuffled, as something to play.
                Log.d(TAG, "Unfocused search without keyword")
                return shuffled()
            }
        } else {
            return focusSearchResult
        }
    }
    companion object {
        private const val TAG = "MusicSource"

        const val STATE_CREATED = 1
        const val STATE_INITIALIZING = 2
        const val STATE_INITIALIZED = 3
        const val STATE_ERROR = 4
    }
}