package com.prosabdev.fluidmusic.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.models.QueueMusicItem
import com.prosabdev.fluidmusic.models.collections.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues


@Database(entities = [SongItem::class, FolderUriTree::class, QueueMusicItem::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songItemDao(): SongItemDao
    abstract fun folderUriTreeDao(): FolderUriTreeDao
    abstract fun queueMusicItemDao(): QueueMusicItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

//        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Since we didn't alter the table, there's nothing else to do here.
//            }
//        }
//        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE users "
//                        + " ADD COLUMN last_update INTEGER")
//            }
//        }
//        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Create the new table
//                database.execSQL(
//                    "CREATE TABLE users_new (userid TEXT, username TEXT, last_update INTEGER, PRIMARY KEY(userid))"
//                )
//                // Copy the data
//                database.execSQL(
//                    "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users"
//                )
//                // Remove the old table
//                database.execSQL("DROP TABLE users")
//                // Change the table name to the correct one
//                database.execSQL("ALTER TABLE users_new RENAME TO users")
//            }
//        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    ConstantValues.FLUID_MUSIC_DATABASE_NAME
                )
//                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}