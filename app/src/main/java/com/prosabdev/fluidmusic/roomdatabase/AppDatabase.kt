package com.prosabdev.fluidmusic.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.PlaylistItem
import com.prosabdev.fluidmusic.models.PlaylistSongItem
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.models.explore.*
import com.prosabdev.fluidmusic.roomdatabase.dao.FolderUriTreeDao
import com.prosabdev.fluidmusic.roomdatabase.dao.PlaylistItemDao
import com.prosabdev.fluidmusic.roomdatabase.dao.QueueMusicItemDao
import com.prosabdev.fluidmusic.roomdatabase.dao.explore.SongItemDao
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
    version = 31)

abstract class AppDatabase : RoomDatabase() {

    abstract fun folderUriTreeDao(): FolderUriTreeDao
    abstract fun playlistItemDao(): PlaylistItemDao
    abstract fun queueMusicItemDao(): QueueMusicItemDao
    abstract fun songItemDao(): SongItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    ConstantValues.FLUID_MUSIC_DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}