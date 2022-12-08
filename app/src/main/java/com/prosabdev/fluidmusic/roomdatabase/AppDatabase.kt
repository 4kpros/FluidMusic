package com.prosabdev.fluidmusic.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.playlist.PlaylistSongItem
import com.prosabdev.fluidmusic.models.queuemusic.QueueMusicItem
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.*
import com.prosabdev.fluidmusic.roomdatabase.dao.FolderUriTreeDao
import com.prosabdev.fluidmusic.roomdatabase.dao.SongItemDao
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.*
import com.prosabdev.fluidmusic.roomdatabase.dao.playlist.PlaylistItemDao
import com.prosabdev.fluidmusic.roomdatabase.dao.playlist.PlaylistSongItemDao
import com.prosabdev.fluidmusic.roomdatabase.dao.queuemusic.QueueMusicItemDao
import com.prosabdev.fluidmusic.utils.ConstantValues

@Database(
    entities = [
        SongItem::class,
        FolderUriTree::class,
        QueueMusicItem::class,
        PlaylistItem::class,
        PlaylistSongItem::class,
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
    version = 77)

abstract class AppDatabase : RoomDatabase() {

    abstract fun folderUriTreeDao(): FolderUriTreeDao
    abstract fun playlistItemDao(): PlaylistItemDao
    abstract fun playlistSongItemDao(): PlaylistSongItemDao
    abstract fun queueMusicItemDao(): QueueMusicItemDao

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
        private var INSTANCE: AppDatabase? = null

        private const val FLUID_MUSIC_DATABASE_NAME = "${ConstantValues.PACKAGE_NAME}.main_database"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    FLUID_MUSIC_DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}