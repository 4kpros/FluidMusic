package com.prosabdev.common.persistence

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prosabdev.common.constants.MainConst
import com.prosabdev.common.models.equalizer.EqualizerPresetBandLevelItem
import com.prosabdev.common.persistence.models.SleepTimerSP
import com.prosabdev.common.persistence.models.SortOrganizeItemSP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PersistentStorage private constructor(private val ctx: Context) {

    var preferences: SharedPreferences = ctx.getSharedPreferences(
        SHARED_PREF_KEY,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val SHARED_PREF_KEY = "${MainConst.PACKAGE_NAME}.SHARED_PREF_KEY"

        @Volatile
        private var INSTANCE: PersistentStorage? = null

        fun getInstance(ctx: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PersistentStorage(ctx).also {
                    INSTANCE = it
                }
            }

        //OPERATE TO BOOLEAN
        fun loadBooleanValue(sharedPrefKey: String, defaultValue: Boolean = false): Boolean {
            return INSTANCE?.preferences?.getBoolean(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveBooleanValue(value: Boolean, sharedPrefKey: String) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putBoolean(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO INT
        fun loadIntValue(sharedPrefKey: String, defaultValue: Int = 0): Int {
            return INSTANCE?.preferences?.getInt(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveIntValue(value: Int, sharedPrefKey: String) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putInt(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO LONG
        fun loadLongValue(sharedPrefKey: String, defaultValue: Long = 0): Long {
            return INSTANCE?.preferences?.getLong(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveLongValue(value: Long, sharedPrefKey: String) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putLong(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO FLOAT
        fun loadFloatValue(sharedPrefKey: String, defaultValue: Float = 0f): Float {
            return INSTANCE?.preferences?.getFloat(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveFloatValue(value: Float, sharedPrefKey: String) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putFloat(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO STRING
        fun loadStringValue(sharedPrefKey: String, defaultValue: String? = null): String? {
            return INSTANCE?.preferences?.getString(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveStringValue(value: String?, sharedPrefKey: String) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putString(sharedPrefKey, value)
            editor.apply()
        }
    }

    class PlayingNow {
        companion object {
            private const val RECENT_SONG_MEDIA_ID_KEY = "RECENT_SONG_MEDIA_ID_KEY"
            private const val RECENT_SONG_TITLE_KEY = "RECENT_SONG_TITLE_KEY"
            private const val RECENT_SONG_SUBTITLE_KEY = "RECENT_SONG_SUBTITLE_KEY"
            private const val RECENT_SONG_POSITION_KEY = "RECENT_SONG_POSITION_KEY"
            private const val RECENT_SONG_ICON_URI_KEY = "RECENT_SONG_ICON_URI_KEY"

            private const val PLAYER_REPEAT = "PLAYER_REPEAT"
            private const val PLAYER_SHUFFLE = "PLAYER_SHUFFLE"

            private const val PLAYER_SLEEP_TIMER = "PLAYER_SLEEP_TIMER"
            private const val PLAYER_PLAYING_PROGRESS_VALUE= "PLAYER_PLAYING_PROGRESS_VALUE"
            private const val PLAYER_QUEUE_LIST_SOURCE = "PLAYER_QUEUE_LIST_SOURCE"
            private const val PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX = "PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX"
            private const val PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE = "PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE"

            suspend fun saveRecentSong(description: MediaDescription?, position: Long) {
                withContext(Dispatchers.IO) {
                    val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return@withContext
                    editor.putString(RECENT_SONG_MEDIA_ID_KEY, description?.mediaId)
                    editor.putString(RECENT_SONG_TITLE_KEY, description?.title?.toString())
                    editor.putString(RECENT_SONG_SUBTITLE_KEY, description?.subtitle?.toString())
                    editor.putString(RECENT_SONG_ICON_URI_KEY, description?.iconUri?.toString())
                    editor.putLong(RECENT_SONG_POSITION_KEY, position)
                    editor.apply()
                }
            }
            fun loadRecentSong(): MediaBrowser.MediaItem? {
                val mediaId = INSTANCE?.preferences?.getString(RECENT_SONG_MEDIA_ID_KEY, null) ?: return null

                val extras = Bundle().also {
                    val position = INSTANCE?.preferences?.getLong(RECENT_SONG_POSITION_KEY, 0L) ?: 0L
                }
                return MediaBrowser.MediaItem(
                    MediaDescription.Builder()
                        .setMediaId(mediaId)
                        .setTitle(INSTANCE?.preferences?.getString(RECENT_SONG_TITLE_KEY, "") ?: "")
                        .setSubtitle(INSTANCE?.preferences?.getString(RECENT_SONG_SUBTITLE_KEY, "") ?: "")
                        .setIconUri(Uri.parse(INSTANCE?.preferences?.getString(RECENT_SONG_ICON_URI_KEY, "") ?: "") ?: Uri.EMPTY)
                        .setExtras(extras)
                        .build(), MediaBrowser.MediaItem.FLAG_PLAYABLE
                )
            }

            //Sleep timer
            fun loadSleepTimer(): SleepTimerSP? {
                val tempGson = Gson()
                val tempItem: String? = INSTANCE?.preferences?.getString(PLAYER_SLEEP_TIMER, null)
                val tempItemType = object : TypeToken<SleepTimerSP>() {}.type
                return tempGson.fromJson<SleepTimerSP>(tempItem, tempItemType)
            }
            fun saveSleepTimer(value: SleepTimerSP?) {
                val tempEditor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(value)
                tempEditor.putString(PLAYER_SLEEP_TIMER, tempJson)
                tempEditor.apply()
            }

            //Repeat
            fun loadRepeat(): Int {
                return loadIntValue(PLAYER_REPEAT, 0)
            }
            fun saveRepeat(value: Int?) {
                saveIntValue(value ?: 0, PLAYER_REPEAT)
            }
            //Shuffle
            fun loadShuffle(): Int {
                return loadIntValue(PLAYER_SHUFFLE, 0)
            }
            fun saveShuffle(value: Int?) {
                saveIntValue(value ?: 0, PLAYER_SHUFFLE)
            }
            //Queue list source
            fun loadQueueListSource(defaultValue: String?): String? {
                return loadStringValue(PLAYER_QUEUE_LIST_SOURCE, defaultValue)
            }
            fun saveQueueListSource(value: String?) {
                saveStringValue(value, PLAYER_QUEUE_LIST_SOURCE)
            }
            //Queue list size
            fun loadPlayingProgressValue(): Long {
                return loadLongValue(PLAYER_PLAYING_PROGRESS_VALUE, 0)
            }
            fun savePlayingProgressValue(value: Long?) {
                saveLongValue(value ?: 0, PLAYER_PLAYING_PROGRESS_VALUE)
            }
            //Queue list source value
            fun loadQueueListSourceColumnIndex(): String? {
                return loadStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX)
            }
            fun saveQueueListSourceColumnIndex(value: String?) {
                saveStringValue(value, PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX)
            }
            //Queue list source value
            fun loadQueueListSourceColumnValue(): String? {
                return loadStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE)
            }
            fun saveQueueListSourceColumnValue(value: String?) {
                saveStringValue(value, PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE)
            }
        }
    }

    class AudioEffects {
        companion object {
            private const val AUDIO_EFFECTS_ENABLE_EQUALIZER = "AUDIO_EFFECTS_ENABLE_EQUALIZER"
            private const val AUDIO_EFFECTS_ENABLE_TONE = "AUDIO_EFFECTS_ENABLE_TONE"
            private const val AUDIO_EFFECTS_ENABLE_BALANCE = "AUDIO_EFFECTS_ENABLE_BALANCE"
            private const val AUDIO_EFFECTS_ENABLE_REVERB = "AUDIO_EFFECTS_ENABLE_REVERB"
            private const val AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER = "AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER"

            private const val AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME = "AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME"
            private const val AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS = "AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS"

            private const val AUDIO_EFFECTS_BASS_PROGRESS = "AUDIO_EFFECTS_BASS_PROGRESS"
            private const val AUDIO_EFFECTS_VISUALIZER_PROGRESS = "AUDIO_EFFECTS_VISUALIZER_PROGRESS"
            private const val AUDIO_EFFECTS_BALANCE_PROGRESS = "AUDIO_EFFECTS_BALANCE_PROGRESS"
            private const val AUDIO_EFFECTS__REVERB_PROGRESS = "AUDIO_EFFECTS__REVERB_PROGRESS"
            private const val AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS = "AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS"

            /**
             * Progress
             **/
            //EQUALIZER PRESET NAME
            fun loadEqualizerPresetName(): String? {
                return loadStringValue(AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME)
            }
            fun saveEqualizerPresetName(value: String?) {
                saveStringValue(value, AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME)
            }
            //EQUALIZER CUSTOM PRESET VALUE
            fun loadEqualizerCustomPresetValue(): ArrayList<EqualizerPresetBandLevelItem>? {
                val tempGson = Gson()
                val tempItem: String? = INSTANCE?.preferences?.getString(
                    AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS, null)
                val tempItemType = object : TypeToken<ArrayList<EqualizerPresetBandLevelItem>?>() {}.type
                return tempGson.fromJson<ArrayList<EqualizerPresetBandLevelItem>?>(tempItem, tempItemType)
            }
            fun saveEqualizerCustomPresetValue(value: ArrayList<EqualizerPresetBandLevelItem>?) {
                val tempEditor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(value)
                tempEditor.putString(AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS, tempJson)
                tempEditor.apply()
            }
            //BASS BOOST PROGRESS
            fun loadBassBoostProgress(): Int {
                return loadIntValue(AUDIO_EFFECTS_BASS_PROGRESS, 0)
            }
            fun saveBassBoostProgress(value: Int) {
                saveIntValue(value, AUDIO_EFFECTS_BASS_PROGRESS)
            }
            //VISUALIZER PROGRESS
            fun loadVisualizerProgress(): Int {
                return loadIntValue(AUDIO_EFFECTS_VISUALIZER_PROGRESS, 0)
            }
            fun saveVisualizerProgress(value: Int) {
                saveIntValue(value, AUDIO_EFFECTS_VISUALIZER_PROGRESS)
            }
            //BALANCE PROGRESS
            fun loadBalanceProgress(): Int {
                return loadIntValue(AUDIO_EFFECTS_BALANCE_PROGRESS, 0)
            }
            fun saveBalanceProgress(value: Int) {
                saveIntValue(value, AUDIO_EFFECTS_BALANCE_PROGRESS)
            }
            //REVERB PROGRESS
            fun loadReverbProgress(): Int {
                return loadIntValue(AUDIO_EFFECTS__REVERB_PROGRESS, 0)
            }
            fun saveReverbProgress(value: Int) {
                saveIntValue(value, AUDIO_EFFECTS__REVERB_PROGRESS)
            }
            //LOUDNESS ENHANCER PROGRESS
            fun loadLoudnessEnhancerProgress(): Int {
                return loadIntValue(AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS, 0)
            }
            fun saveLoudnessEnhancerProgress(value: Int) {
                saveIntValue(value, AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS)
            }

            /**
             * States
             **/
            //EQUALIZER STATE SETTING
            fun loadEqualizerState(): Boolean {
                return loadBooleanValue(AUDIO_EFFECTS_ENABLE_EQUALIZER, false)
            }
            fun saveEqualizerState(value: Boolean) {
                saveBooleanValue(value, AUDIO_EFFECTS_ENABLE_EQUALIZER)
            }
            //TONE STATE SETTING
            fun loadToneState(): Boolean {
                return loadBooleanValue(AUDIO_EFFECTS_ENABLE_TONE, false)
            }
            fun saveToneState(value: Boolean) {
                saveBooleanValue(value, AUDIO_EFFECTS_ENABLE_TONE)
            }
            //BALANCE STATE SETTING
            fun loadBalanceState(): Boolean {
                return loadBooleanValue(AUDIO_EFFECTS_ENABLE_BALANCE, false)
            }
            fun saveBalanceState(value: Boolean) {
                saveBooleanValue(value, AUDIO_EFFECTS_ENABLE_BALANCE)
            }
            //REVERB STATE SETTING
            fun loadReverbState(): Boolean {
                return loadBooleanValue(AUDIO_EFFECTS_ENABLE_REVERB, false)
            }
            fun saveReverbState(value: Boolean) {
                saveBooleanValue(value, AUDIO_EFFECTS_ENABLE_REVERB)
            }
            //LOUDNESS ENHANCER STATE SETTING
            fun loadLoudnessEnhancerState(): Boolean {
                return loadBooleanValue(AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER, false)
            }
            fun saveLoudnessEnhancerState(value: Boolean) {
                saveBooleanValue(value, AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER)
            }
        }
    }

    class Settings {
        companion object {
            private const val SETTINGS_FIRST_TIME_LOADING = "SETTINGS_FIRST_TIME_LOADING"

            fun loadIsFirstTimeOpenApp(): Boolean {
                return loadBooleanValue(SETTINGS_FIRST_TIME_LOADING, true)
            }
            fun saveIsFirstTimeOpenApp(value: Boolean) {
                saveBooleanValue(value, SETTINGS_FIRST_TIME_LOADING)
            }
        }
    }

    class SortAnOrganizeForExploreContents {
        companion object {
            const val SORT_ORGANIZE_PLAYER_QUEUE_MUSIC = "SORT_ORGANIZE_PLAYER_QUEUE_MUSIC"

            const val SORT_ORGANIZE_ALL_SONGS = "SORT_ORGANIZE_ALL_SONGS"
            const val SORT_ORGANIZE_ALBUM_ARTISTS = "SORT_ORGANIZE_ALBUM_ARTISTS"
            const val SORT_ORGANIZE_ALBUMS = "SORT_ORGANIZE_ALBUMS"
            const val SORT_ORGANIZE_ARTISTS = "SORT_ORGANIZE_ARTISTS"
            const val SORT_ORGANIZE_COMPOSERS = "SORT_ORGANIZE_COMPOSERS"
            const val SORT_ORGANIZE_FOLDERS = "SORT_ORGANIZE_FOLDERS"
            const val SORT_ORGANIZE_GENRES = "SORT_ORGANIZE_GENRES"
            const val SORT_ORGANIZE_YEARS = "SORT_ORGANIZE_YEARS"

            const val SORT_ORGANIZE_FOLDER_HIERARCHY = "SORT_ORGANIZE_FOLDER_HIERARCHY"
            const val SORT_ORGANIZE_FOLDER_HIERARCHY_MUSIC_CONTENT = "SORT_ORGANIZE_FOLDER_HIERARCHY_MUSIC_CONTENT"
            const val SORT_ORGANIZE_PLAYLISTS = "SORT_ORGANIZE_PLAYLISTS"
            const val SORT_ORGANIZE_PLAYLIST_MUSIC_CONTENT = "SORT_ORGANIZE_PLAYLIST_MUSIC_CONTENT"
            const val SORT_ORGANIZE_STREAMS = "SORT_ORGANIZE_STREAMS"
            const val SORT_ORGANIZE_STREAM_MUSIC_CONTENT = "SORT_ORGANIZE_STREAM_MUSIC_CONTENT"

            const val SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST = "SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST"
            const val SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM = "SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM"
            const val SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST = "SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST"
            const val SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER = "SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER"
            const val SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER = "SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER"
            const val SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE = "SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE"
            const val SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_YEAR = "SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_YEAR"

            fun loadSortOrganizeItemsFor(sharedPrefsKey: String): SortOrganizeItemSP? {
                val tempGson = Gson()
                val tempItem: String? = INSTANCE?.preferences?.getString(sharedPrefsKey, null)
                val tempItemType = object : TypeToken<SortOrganizeItemSP>() {}.type
                return tempGson.fromJson<SortOrganizeItemSP>(tempItem, tempItemType)
            }
            fun saveSortOrganizeItemsFor(sharedPrefsKey: String, sortOrganizeItemSP: SortOrganizeItemSP?) {
                val tempEditor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(sortOrganizeItemSP)
                tempEditor.putString(sharedPrefsKey, tempJson)
                tempEditor.apply()
            }
        }
    }
}