package com.prosabdev.fluidmusic.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.sharedprefs.models.SleepTimerSP
import com.prosabdev.fluidmusic.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.utils.ConstantValues

abstract class SharedPreferenceManagerUtils {
    class Player {
        companion object {
            private const val SHARED_PREFERENCES_REPEAT = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_REPEAT"
            private const val SHARED_PREFERENCES_SHUFFLE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SHUFFLE"

            private const val SHARED_PREFERENCES_SLEEP_TIMER = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SLEEP_TIMER"
            private const val SHARED_PREFERENCES_CURRENT_PLAYING_SONG = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_CURRENT_PLAYING_SONG"
            private const val SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE= "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE"
            private const val SHARED_PREFERENCES_QUEUE_LIST_SOURCE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_QUEUE_LIST_SOURCE"
            private const val SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE"

            //Current song value
            fun loadCurrentPlayingSong(ctx: Context, sharedPreferences: SharedPreferences? = null): SongItem? {
                if(sharedPreferences != null){
                    val tempGson: Gson = Gson()
                    val tempItem: String? = sharedPreferences.getString(
                        SHARED_PREFERENCES_CURRENT_PLAYING_SONG, null)
                    val tempItemType = object : TypeToken<SongItem>() {}.type
                    Log.i(ConstantValues.TAG, "Current playing song loaded !")
                    return tempGson.fromJson<SongItem>(tempItem, tempItemType)
                }
                val tempGson: Gson = Gson()
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(SHARED_PREFERENCES_CURRENT_PLAYING_SONG, null)
                val tempItemType = object : TypeToken<SongItem>() {}.type
                Log.i(ConstantValues.TAG, "Current playing song loaded !")
                return tempGson.fromJson<SongItem>(tempItem, tempItemType)
            }
            fun saveCurrentPlayingSong(ctx: Context, songItem: SongItem?) {
                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson: Gson = Gson()
                val tempJson: String = tempGson.toJson(songItem)
                tempEditor.putString(SHARED_PREFERENCES_CURRENT_PLAYING_SONG, tempJson)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Current playing song saved !")
            }

            //Sleep timer
            fun loadSleepTimer(ctx: Context, sharedPreferences: SharedPreferences? = null): SleepTimerSP? {
                if(sharedPreferences != null){
                    val tempGson: Gson = Gson()
                    val tempItem: String? = sharedPreferences.getString(
                        SHARED_PREFERENCES_SLEEP_TIMER, null)
                    val tempItemType = object : TypeToken<SleepTimerSP>() {}.type
                    Log.i(ConstantValues.TAG, "Sleep timer loaded !")
                    return tempGson.fromJson<SleepTimerSP>(tempItem, tempItemType)
                }
                val tempGson: Gson = Gson()
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(SHARED_PREFERENCES_SLEEP_TIMER, null)
                val tempItemType = object : TypeToken<SleepTimerSP>() {}.type
                Log.i(ConstantValues.TAG, "Sleep timer loaded !")
                return tempGson.fromJson<SleepTimerSP>(tempItem, tempItemType)
            }
            fun saveSleepTimer(ctx: Context, sleepTimerSP: SleepTimerSP?) {
                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson: Gson = Gson()
                val tempJson: String = tempGson.toJson(sleepTimerSP)
                tempEditor.putString(SHARED_PREFERENCES_SLEEP_TIMER, tempJson)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Sleep timer saved !")
            }

            //Repeat
            fun loadRepeat(ctx: Context, sharedPreferences: SharedPreferences? = null): Int {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Current playing song seek position loaded !")
                    return sharedPreferences.getInt(
                        SHARED_PREFERENCES_REPEAT,
                        PlaybackStateCompat.REPEAT_MODE_NONE
                    )
                }
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Repeat loaded !")
                return tempSP.getInt(SHARED_PREFERENCES_REPEAT, PlaybackStateCompat.REPEAT_MODE_NONE)
            }
            fun saveRepeat(ctx: Context, value: Int?) {
                val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putInt(SHARED_PREFERENCES_REPEAT, value ?: PlaybackStateCompat.REPEAT_MODE_NONE)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Repeat saved !")
            }

            //Shuffle
            fun loadShuffle(ctx: Context, sharedPreferences: SharedPreferences? = null): Int {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Shuffle loaded !")
                    return sharedPreferences.getInt(
                        SHARED_PREFERENCES_SHUFFLE,
                        PlaybackStateCompat.SHUFFLE_MODE_NONE
                    )
                }
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Shuffle loaded !")
                return tempSP.getInt(SHARED_PREFERENCES_SHUFFLE, PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
            fun saveShuffle(ctx: Context, value: Int?) {
                val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putInt(SHARED_PREFERENCES_SHUFFLE, value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Shuffle saved !")
            }

            //Queue list source
            fun loadQueueListSource(ctx: Context, sharedPreferences: SharedPreferences? = null): String? {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Queue list source loaded !")
                    return sharedPreferences.getString(
                        SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
                        AllSongsFragment.TAG
                    )
                }
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Queue list source loaded !")
                return tempSP.getString(
                    SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
                    AllSongsFragment.TAG
                )
            }
            fun saveQueueListSource(ctx: Context, value: String?) {
                val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putString(SHARED_PREFERENCES_QUEUE_LIST_SOURCE, value)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Queue list source saved !")
            }

            //Queue list size
            fun loadPlayingProgressValue(ctx: Context, sharedPreferences: SharedPreferences? = null): Long {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Playing progress value loaded !")
                    return sharedPreferences.getLong(
                        SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE,
                        0
                    )
                }
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Playing progress value loaded !")
                return tempSP.getLong(
                    SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE,
                    0
                )
            }
            fun savePlayingProgressValue(ctx: Context, value: Long?) {
                val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putLong(SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE, value ?: 0)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Playing progress value saved !")
            }

            //Queue list source value
            fun loadQueueListSourceValue(ctx: Context, sharedPreferences: SharedPreferences? = null): String? {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Queue list source value loaded !")
                    return sharedPreferences.getString(
                        SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
                        null
                    )
                }
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Queue list source value loaded !")
                return tempSP.getString(
                    SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
                    null
                )
            }
            fun saveQueueListSourceValue(ctx: Context, value: String?) {
                val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putString(SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE, value)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Queue list source value saved !")
            }
        }
    }

    class SortAnOrganizeForExploreContents {
        companion object {
            const val SHARED_PREFERENCES_SORT_ORGANIZE_PLAYER_QUEUE_MUSIC = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_PLAYER_QUEUE_MUSIC"

            const val SHARED_PREFERENCES_SORT_ORGANIZE_ALL_SONGS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_ALL_SONGS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_ALBUM_ARTISTS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_ALBUM_ARTISTS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_ALBUMS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_ALBUMS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_ARTISTS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_ARTISTS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_COMPOSERS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_COMPOSERS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_FOLDERS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_FOLDERS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_GENRES = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_GENRES"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_YEARS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_YEARS"

            const val SHARED_PREFERENCES_SORT_ORGANIZE_FOLDER_HIERARCHY = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_FOLDER_HIERARCHY"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_FOLDER_HIERARCHY_MUSIC_CONTENT = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_FOLDER_HIERARCHY_MUSIC_CONTENT"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_PLAYLISTS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_PLAYLISTS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_PLAYLIST_MUSIC_CONTENT = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_PLAYLIST_MUSIC_CONTENT"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_STREAMS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_STREAMS"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_STREAM_MUSIC_CONTENT = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_STREAM_MUSIC_CONTENT"

            const val SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE"
            const val SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_YEAR = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_YEAR"

            fun loadSortOrganizeItemsFor(ctx: Context, sharedPrefsKey: String, sharedPreferences: SharedPreferences? = null): SortOrganizeItemSP? {
                if(sharedPreferences != null){
                    val tempGson: Gson = Gson()
                    val tempItem: String? = sharedPreferences.getString(sharedPrefsKey, null)
                    val tempItemType = object : TypeToken<SortOrganizeItemSP>() {}.type
                    Log.i(ConstantValues.TAG, "Sort and organize items for $sharedPrefsKey loaded !")
                    return tempGson.fromJson<SortOrganizeItemSP>(tempItem, tempItemType)
                }
                val tempGson: Gson = Gson()
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(sharedPrefsKey, null)
                val tempItemType = object : TypeToken<SortOrganizeItemSP>() {}.type
                Log.i(ConstantValues.TAG, "Sort and organize items for $sharedPrefsKey loaded !")
                Log.i(ConstantValues.TAG, "${tempGson.fromJson<SortOrganizeItemSP>(tempItem, tempItemType)}")
                return tempGson.fromJson<SortOrganizeItemSP>(tempItem, tempItemType)
            }
            fun saveSortOrganizeItemsFor(ctx: Context, sharedPrefsKey: String, sortOrganizeItemSP: SortOrganizeItemSP?) {
                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson: Gson = Gson()
                val tempJson: String = tempGson.toJson(sortOrganizeItemSP)
                tempEditor.putString(sharedPrefsKey, tempJson)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Sort and organize items for $sharedPrefsKey saved !")
            }
        }
    }
    class Settings {
        companion object {
            const val SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING"

            fun loadIsFirstTimeOpenApp(ctx: Context): Boolean {
                val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                return sharedPref.getBoolean(SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING, true)
            }
            fun saveIsFirstTimeOpenApp(ctx: Context, value: Boolean) {
                val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putBoolean(SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING, value)
                editor.apply()
            }
        }
    }
}