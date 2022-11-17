package com.prosabdev.fluidmusic.models.collections

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prosabdev.fluidmusic.utils.ConstantValues
import org.jaudiotagger.tag.images.Artwork

@Entity(
    tableName = ConstantValues.FLUID_MUSIC_SONG_TABLE,
    indices = [Index(value = ["id", "absolutePath"], unique = true)]
)//room ; to create sqlite objects
open class SongItem() {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
    var absolutePath: String = ""
    var fileName: String? = null
    var title: String? = null
    var artist: String? = null
    var albumArtist: String? = null
    var album: String? = null
    var genre: String? = null
    var relativePath: String? = null
    var folder: String? = null
    var year: String? = null
    var duration: Long = 0

    //
    var typeMime: String? = null
    var sampleRate = 0
    var bitrate = 0.0

    //
    var lastUpdateDate: String? = null
    var lastAddedDate: String? = null

    //
    var storageName: String? = null

    //
    @Ignore
    var covertArt: Artwork? = null
    @Ignore
    var covertArtUrl: String? = null
}