package com.prosabdev.common.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prosabdev.common.models.FolderUriTree
import com.prosabdev.common.models.equalizer.EqualizerPresetBandLevelItem
import com.prosabdev.common.models.equalizer.EqualizerPresetItem
import com.prosabdev.common.models.playlist.PlaylistItem
import com.prosabdev.common.models.playlist.PlaylistSongItem
import com.prosabdev.common.models.queuemusic.QueueMusicItem
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.view.*
import com.prosabdev.common.roomdatabase.dao.FolderUriTreeDao
import com.prosabdev.common.roomdatabase.dao.SongItemDao
import com.prosabdev.common.roomdatabase.dao.equalizer.EqualizerPresetBandLevelItemDao
import com.prosabdev.common.roomdatabase.dao.equalizer.EqualizerPresetItemDao
import com.prosabdev.common.roomdatabase.dao.explore.*
import com.prosabdev.common.roomdatabase.dao.playlist.PlaylistItemDao
import com.prosabdev.common.roomdatabase.dao.playlist.PlaylistSongItemDao
import com.prosabdev.common.roomdatabase.dao.queuemusic.QueueMusicItemDao
import com.prosabdev.common.constants.MainConst

@Database(
    entities = [
        SongItem::class,
        FolderUriTree::class,
        QueueMusicItem::class,
        PlaylistItem::class,
        PlaylistSongItem::class,
        EqualizerPresetItem::class,
        EqualizerPresetBandLevelItem::class,
               ],
    views = [
        AlbumArtistItem::class,
        AlbumItem::class,
        ArtistItem::class,
        ComposerItem::class,
        FolderItem::class,
        GenreItem::class,
        YearItem::class,
           ],
    version = 90)

abstract class AppDatabase : RoomDatabase() {

    abstract fun folderUriTreeDao(): FolderUriTreeDao
    abstract fun playlistItemDao(): PlaylistItemDao
    abstract fun playlistSongItemDao(): PlaylistSongItemDao
    abstract fun queueMusicItemDao(): QueueMusicItemDao
    abstract fun equalizerPresetItemDao(): EqualizerPresetItemDao
    abstract fun equalizerPresetBandLevelItemDao(): EqualizerPresetBandLevelItemDao

    abstract fun albumArtistItemDao(): AlbumArtistItemDao
    abstract fun albumItemDao(): AlbumItemDao
    abstract fun artistItemDao(): ArtistItemDao
    abstract fun composerItemDao(): ComposerItemDao
    abstract fun folderItemDao(): FolderItemDao
    abstract fun genreItemDao(): GenreItemDao
    abstract fun songItemDao(): SongItemDao
    abstract fun yearItemDao(): YearItemDao

    companion object {
        @Volatile
        private var mInstance: AppDatabase? = null

        private const val FLUID_MUSIC_DATABASE_NAME = "${MainConst.PACKAGE_NAME}.fluidmusic_sqlite"

        fun getDatabase(ctx: Context): AppDatabase {
            return mInstance
                ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx,
                    AppDatabase::class.java,
                    FLUID_MUSIC_DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    mInstance = instance

                instance
            }
        }
    }
}