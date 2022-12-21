package com.prosabdev.fluidmusic.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.equalizer.EqualizerPresetBandLevelItem
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.sharedprefs.models.SleepTimerSP
import com.prosabdev.fluidmusic.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.utils.ConstantValues

abstract class SharedPreferenceManagerUtils {
    class Player {
        companion object {
            private const val SHARED_PREFERENCES_PLAYER_REPEAT = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_REPEAT"
            private const val SHARED_PREFERENCES_PLAYER_SHUFFLE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_SHUFFLE"

            private const val SHARED_PREFERENCES_PLAYER_SLEEP_TIMER = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_SLEEP_TIMER"
            private const val SHARED_PREFERENCES_PLAYER_CURRENT_PLAYING_SONG = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_CURRENT_PLAYING_SONG"
            private const val SHARED_PREFERENCES_PLAYER_PLAYING_PROGRESS_VALUE= "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_PLAYING_PROGRESS_VALUE"
            private const val SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE"
            private const val SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX"
            private const val SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE"

            //Current song value
            fun loadCurrentPlayingSong(ctx: Context, sharedPreferences: SharedPreferences? = null): SongItem? {
                if(sharedPreferences != null){
                    val tempGson = Gson()
                    val tempItem: String? = sharedPreferences.getString(
                        SHARED_PREFERENCES_PLAYER_CURRENT_PLAYING_SONG, null)
                    val tempItemType = object : TypeToken<SongItem>() {}.type
                    return tempGson.fromJson<SongItem>(tempItem, tempItemType)
                }
                val tempGson = Gson()
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(SHARED_PREFERENCES_PLAYER_CURRENT_PLAYING_SONG, null)
                val tempItemType = object : TypeToken<SongItem>() {}.type
                return tempGson.fromJson<SongItem>(tempItem, tempItemType)
            }
            fun saveCurrentPlayingSong(ctx: Context, value: SongItem?) {
                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(value)
                tempEditor.putString(SHARED_PREFERENCES_PLAYER_CURRENT_PLAYING_SONG, tempJson)
                tempEditor.apply()
            }

            //Sleep timer
            fun loadSleepTimer(ctx: Context, sharedPreferences: SharedPreferences? = null): SleepTimerSP? {
                if(sharedPreferences != null){
                    val tempGson = Gson()
                    val tempItem: String? = sharedPreferences.getString(
                        SHARED_PREFERENCES_PLAYER_SLEEP_TIMER, null)
                    val tempItemType = object : TypeToken<SleepTimerSP>() {}.type
                    return tempGson.fromJson<SleepTimerSP>(tempItem, tempItemType)
                }
                val tempGson = Gson()
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(SHARED_PREFERENCES_PLAYER_SLEEP_TIMER, null)
                val tempItemType = object : TypeToken<SleepTimerSP>() {}.type
                return tempGson.fromJson<SleepTimerSP>(tempItem, tempItemType)
            }
            fun saveSleepTimer(ctx: Context, value: SleepTimerSP?) {
                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(value)
                tempEditor.putString(SHARED_PREFERENCES_PLAYER_SLEEP_TIMER, tempJson)
                tempEditor.apply()
            }

            //Repeat
            fun loadRepeat(ctx: Context, sharedPreferences: SharedPreferences? = null): Int {
                return loadIntValue(ctx, SHARED_PREFERENCES_PLAYER_REPEAT, PlaybackStateCompat.REPEAT_MODE_NONE, sharedPreferences)
            }
            fun saveRepeat(ctx: Context, value: Int?) {
                saveIntValue(ctx, value ?: PlaybackStateCompat.REPEAT_MODE_NONE, SHARED_PREFERENCES_PLAYER_REPEAT)
            }
            //Shuffle
            fun loadShuffle(ctx: Context, sharedPreferences: SharedPreferences? = null): Int {
                return loadIntValue(ctx, SHARED_PREFERENCES_PLAYER_SHUFFLE, PlaybackStateCompat.SHUFFLE_MODE_NONE, sharedPreferences)
            }
            fun saveShuffle(ctx: Context, value: Int?) {
                saveIntValue(ctx, value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE, SHARED_PREFERENCES_PLAYER_SHUFFLE)
            }
            //Queue list source
            fun loadQueueListSource(ctx: Context, sharedPreferences: SharedPreferences? = null): String? {
                return loadStringValue(ctx, SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE, AllSongsFragment.TAG, sharedPreferences)
            }
            fun saveQueueListSource(ctx: Context, value: String?) {
                saveStringValue(ctx, value, SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE)
            }
            //Queue list size
            fun loadPlayingProgressValue(ctx: Context, sharedPreferences: SharedPreferences? = null): Long {
                return loadLongValue(ctx, SHARED_PREFERENCES_PLAYER_PLAYING_PROGRESS_VALUE, 0, sharedPreferences)
            }
            fun savePlayingProgressValue(ctx: Context, value: Long?) {
                saveLongValue(ctx, value ?: 0, SHARED_PREFERENCES_PLAYER_PLAYING_PROGRESS_VALUE)
            }
            //Queue list source value
            fun loadQueueListSourceColumnIndex(ctx: Context, sharedPreferences: SharedPreferences? = null): String? {
                return loadStringValue(ctx, SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX, null, sharedPreferences)
            }
            fun saveQueueListSourceColumnIndex(ctx: Context, value: String?) {
                saveStringValue(ctx, value, SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX)
            }
            //Queue list source value
            fun loadQueueListSourceColumnValue(ctx: Context, sharedPreferences: SharedPreferences? = null): String? {
                return loadStringValue(ctx, SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE, null, sharedPreferences)
            }
            fun saveQueueListSourceColumnValue(ctx: Context, value: String?) {
                saveStringValue(ctx, value, SHARED_PREFERENCES_PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE)
            }
        }
    }

    class AudioEffects {
        companion object {
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_EQUALIZER = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_EQUALIZER"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_TONE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_TONE"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_BALANCE = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_BALANCE"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_REVERB = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_REVERB"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER"

            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS"

            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_BASS_PROGRESS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_BASS_PROGRESS"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_VISUALIZER_PROGRESS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_VISUALIZER_PROGRESS"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_BALANCE_PROGRESS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_BALANCE_PROGRESS"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS__REVERB_PROGRESS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS__REVERB_PROGRESS"
            private const val SHARED_PREFERENCES_AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS"

            /**
             * Progress
             **/
            //EQUALIZER PRESET NAME
            fun loadEqualizerPresetName(ctx: Context): String? {
                return loadStringValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME, null, null)
            }
            fun saveEqualizerPresetName(ctx: Context, value: String?) {
                saveStringValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME)
            }
            //EQUALIZER CUSTOM PRESET VALUE
            fun loadEqualizerCustomPresetValue(ctx: Context): ArrayList<EqualizerPresetBandLevelItem>? {
                val tempGson = Gson()
                val tempSP: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(SHARED_PREFERENCES_AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS, null)
                val tempItemType = object : TypeToken<ArrayList<EqualizerPresetBandLevelItem>?>() {}.type
                return tempGson.fromJson<ArrayList<EqualizerPresetBandLevelItem>?>(tempItem, tempItemType)
            }
            fun saveEqualizerCustomPresetValue(ctx: Context, value: ArrayList<EqualizerPresetBandLevelItem>?) {
                val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(
                    ctx.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(value)
                tempEditor.putString(SHARED_PREFERENCES_AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS, tempJson)
                tempEditor.apply()
            }
            //BASS BOOST PROGRESS
            fun loadBassBoostProgress(ctx: Context): Int {
                return loadIntValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_BASS_PROGRESS, 0, null)
            }
            fun saveBassBoostProgress(ctx: Context, value: Int) {
                saveIntValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_BASS_PROGRESS)
            }
            //VISUALIZER PROGRESS
            fun loadVisualizerProgress(ctx: Context): Int {
                return loadIntValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_VISUALIZER_PROGRESS, 0, null)
            }
            fun saveVisualizerProgress(ctx: Context, value: Int) {
                saveIntValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_VISUALIZER_PROGRESS)
            }
            //BALANCE PROGRESS
            fun loadBalanceProgress(ctx: Context): Int {
                return loadIntValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_BALANCE_PROGRESS, 0, null)
            }
            fun saveBalanceProgress(ctx: Context, value: Int) {
                saveIntValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_BALANCE_PROGRESS)
            }
            //REVERB PROGRESS
            fun loadReverbProgress(ctx: Context): Int {
                return loadIntValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS__REVERB_PROGRESS, 0, null)
            }
            fun saveReverbProgress(ctx: Context, value: Int) {
                saveIntValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS__REVERB_PROGRESS)
            }
            //LOUDNESS ENHANCER PROGRESS
            fun loadLoudnessEnhancerProgress(ctx: Context): Int {
                return loadIntValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS, 0, null)
            }
            fun saveLoudnessEnhancerProgress(ctx: Context, value: Int) {
                saveIntValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS)
            }

            /**
             * States
             **/
            //EQUALIZER STATE SETTING
            fun loadEqualizerState(ctx: Context): Boolean {
                return loadBooleanValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_EQUALIZER, false, null)
            }
            fun saveEqualizerState(ctx: Context, value: Boolean) {
                saveBooleanValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_EQUALIZER)
            }
            //TONE STATE SETTING
            fun loadToneState(ctx: Context): Boolean {
                return loadBooleanValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_TONE, false, null)
            }
            fun saveToneState(ctx: Context, value: Boolean) {
                saveBooleanValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_TONE)
            }
            //BALANCE STATE SETTING
            fun loadBalanceState(ctx: Context): Boolean {
                return loadBooleanValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_BALANCE, false, null)
            }
            fun saveBalanceState(ctx: Context, value: Boolean) {
                saveBooleanValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_BALANCE)
            }
            //REVERB STATE SETTING
            fun loadReverbState(ctx: Context): Boolean {
                return loadBooleanValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_REVERB, false, null)
            }
            fun saveReverbState(ctx: Context, value: Boolean) {
                saveBooleanValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_REVERB)
            }
            //LOUDNESS ENHANCER STATE SETTING
            fun loadLoudnessEnhancerState(ctx: Context): Boolean {
                return loadBooleanValue(ctx, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER, false, null)
            }
            fun saveLoudnessEnhancerState(ctx: Context, value: Boolean) {
                saveBooleanValue(ctx, value, SHARED_PREFERENCES_AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER)
            }
        }
    }

    class Settings {
        companion object {
            private const val SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING = "${ConstantValues.PACKAGE_NAME}.SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING"

            fun loadIsFirstTimeOpenApp(ctx: Context): Boolean {
                return loadBooleanValue(ctx, SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING, true, null)
            }
            fun saveIsFirstTimeOpenApp(ctx: Context, value: Boolean) {
                saveBooleanValue(ctx, value, SHARED_PREFERENCES_SETTINGS_FIRST_TIME_LOADING)
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
                    val tempGson = Gson()
                    val tempItem: String? = sharedPreferences.getString(sharedPrefsKey, null)
                    val tempItemType = object : TypeToken<SortOrganizeItemSP>() {}.type
                    Log.i(ConstantValues.TAG, "Sort and organize items for $sharedPrefsKey loaded !")
                    return tempGson.fromJson<SortOrganizeItemSP>(tempItem, tempItemType)
                }
                val tempGson = Gson()
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
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(sortOrganizeItemSP)
                tempEditor.putString(sharedPrefsKey, tempJson)
                tempEditor.apply()
            }
        }
    }


    companion object {
        //OPERATE TO BOOLEAN
        fun loadBooleanValue(ctx: Context, sharedPrefKey: String, defaultValue: Boolean = false, sharedPreferences: SharedPreferences? = null): Boolean {
            if(sharedPreferences != null){
                return sharedPreferences.getBoolean(sharedPrefKey, defaultValue)
            }
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean(sharedPrefKey, defaultValue)
        }
        fun saveBooleanValue(ctx: Context, value: Boolean, sharedPrefKey: String) {
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO INT
        fun loadIntValue(ctx: Context, sharedPrefKey: String, defaultValue: Int = 0, sharedPreferences: SharedPreferences? = null): Int {
            if(sharedPreferences != null){
                return sharedPreferences.getInt(sharedPrefKey, defaultValue)
            }
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getInt(sharedPrefKey, defaultValue)
        }
        fun saveIntValue(ctx: Context, value: Int, sharedPrefKey: String) {
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO LONG
        fun loadLongValue(ctx: Context, sharedPrefKey: String, defaultValue: Long = 0, sharedPreferences: SharedPreferences? = null): Long {
            if(sharedPreferences != null){
                return sharedPreferences.getLong(sharedPrefKey, defaultValue)
            }
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getLong(sharedPrefKey, defaultValue)
        }
        fun saveLongValue(ctx: Context, value: Long, sharedPrefKey: String) {
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putLong(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO FLOAT
        fun loadFloatValue(ctx: Context, sharedPrefKey: String, defaultValue: Float = 0f, sharedPreferences: SharedPreferences? = null): Float {
            if(sharedPreferences != null){
                return sharedPreferences.getFloat(sharedPrefKey, defaultValue)
            }
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getFloat(sharedPrefKey, defaultValue)
        }
        fun saveFloatValue(ctx: Context, value: Float, sharedPrefKey: String) {
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putFloat(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO STRING
        fun loadStringValue(ctx: Context, sharedPrefKey: String, defaultValue: String? = null, sharedPreferences: SharedPreferences? = null): String? {
            if(sharedPreferences != null){
                return sharedPreferences.getString(sharedPrefKey, defaultValue)
            }
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getString(sharedPrefKey, defaultValue)
        }
        fun saveStringValue(ctx: Context, value: String?, sharedPrefKey: String) {
            val sharedPref: SharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(sharedPrefKey, value)
            editor.apply()
        }
    }
}