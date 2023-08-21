package com.prosabdev.common.library

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat

/**
 * Interface used by [MusicService] for looking up [MediaMetadataCompat] objects.
 *
 * Because Kotlin provides methods such as [Iterable.find] and [Iterable.filter],
 * this is a convenient interface to have on sources.
 */
interface IMusicSource : Iterable<MediaMetadataCompat> {
    suspend fun load()
    fun whenReady(performAction: (Boolean) -> Unit): Boolean
    fun search(query: String, extras: Bundle): List<MediaMetadataCompat>
}