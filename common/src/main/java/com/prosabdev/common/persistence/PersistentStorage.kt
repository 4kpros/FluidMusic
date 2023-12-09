package com.prosabdev.common.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.media3.common.Player
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
        fun saveBooleanValue(sharedPrefKey: String, value: Boolean) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putBoolean(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO INT
        fun loadIntValue(sharedPrefKey: String, defaultValue: Int = 0): Int {
            return INSTANCE?.preferences?.getInt(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveIntValue(sharedPrefKey: String, value: Int) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putInt(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO LONG
        fun loadLongValue(sharedPrefKey: String, defaultValue: Long = 0): Long {
            return INSTANCE?.preferences?.getLong(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveLongValue(sharedPrefKey: String, value: Long?) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putLong(sharedPrefKey, value ?: 0)
            editor.apply()
        }
        //OPERATE TO FLOAT
        fun loadFloatValue(sharedPrefKey: String, defaultValue: Float = 0f): Float {
            return INSTANCE?.preferences?.getFloat(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveFloatValue(sharedPrefKey: String, value: Float) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putFloat(sharedPrefKey, value)
            editor.apply()
        }
        //OPERATE TO STRING
        fun loadStringValue(sharedPrefKey: String, defaultValue: String? = null): String? {
            return INSTANCE?.preferences?.getString(sharedPrefKey, defaultValue) ?: defaultValue
        }
        fun saveStringValue(sharedPrefKey: String, value: String?) {
            val editor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            editor.putString(sharedPrefKey, value)
            editor.apply()
        }

        fun loadCustomObjectValue(sharedPrefKey: String): Any? {
            val tempGson = Gson()
            val tempItem: String? = INSTANCE?.preferences?.getString(
                sharedPrefKey, null)
            val tempItemType = object : TypeToken<Any?>() {}.type
            return tempGson.fromJson<Any?>(tempItem, tempItemType)
        }
        fun saveCustomObjectValue(sharedPrefKey: String, value: Any?) {
            val tempEditor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
            val tempGson = Gson()
            val tempJson: String = tempGson.toJson(value)
            tempEditor.putString(sharedPrefKey, tempJson)
            tempEditor.apply()
        }
    }

    class Playback {
        companion object {
            private const val CURRENT_MEDIA_ITEM_ID_KEY = "CURRENT_MEDIA_ITEM_ID_KEY"

            private const val CURRENT_POSITION_MS = "CURRENT_POSITION_MS"

            private const val REPEAT_MODE = "REPEAT_MODE"
            private const val SHUFFLE_MODE_ENABLED = "SHUFFLE_MODE_ENABLED"

            private const val PLAYBACK_SPEED = "PLAYBACK_SPEED"
            private const val PLAYBACK_PITCH = "PLAYBACK_PITCH"

            private const val SLEEP_TIMER = "SLEEP_TIMER"

            private const val PLAYER_QUEUE_LIST_SOURCE = "PLAYER_QUEUE_LIST_SOURCE"
            private const val PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX = "PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX"
            private const val PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE = "PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE"

            var currentMediaId: String?
                get() = loadStringValue(CURRENT_MEDIA_ITEM_ID_KEY, null)
                set(value) = saveStringValue(CURRENT_MEDIA_ITEM_ID_KEY, value)

            var currentPositionMs: Long?
                get() = loadLongValue(CURRENT_POSITION_MS, 0)
                set(value) = saveLongValue(CURRENT_POSITION_MS, value)

            var repeatMode: Int?
                get() = loadIntValue(REPEAT_MODE, Player.REPEAT_MODE_OFF)
                set(value) = saveIntValue(REPEAT_MODE, value ?: Player.REPEAT_MODE_OFF)

            var shuffleModeEnabled: Boolean?
                get() = loadBooleanValue(SHUFFLE_MODE_ENABLED, false)
                set(value) = saveBooleanValue(SHUFFLE_MODE_ENABLED, value ?: false)

            var playbackSpeed: Float?
                get() = loadFloatValue(PLAYBACK_SPEED, 0f)
                set(value) = saveFloatValue(PLAYBACK_SPEED, value ?: 0f)

            var playbackPitch: Float?
                get() = loadFloatValue(PLAYBACK_PITCH, 0f)
                set(value) = saveFloatValue(PLAYBACK_PITCH, value ?: 0f)

            var sleepTimer: Float?
                get() = loadFloatValue(SLEEP_TIMER, 0f)
                set(value) = saveFloatValue(SLEEP_TIMER, value ?: 0f)

            var queueListSource: String?
                get() = loadStringValue(PLAYER_QUEUE_LIST_SOURCE, null)
                set(value) = saveStringValue(PLAYER_QUEUE_LIST_SOURCE, value)

            var queueListSourceColumnIndex: String?
                get() = loadStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX, null)
                set(value) = saveStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX, value)

            var queueListSourceColumnValue: String?
                get() = loadStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE, null)
                set(value) = saveStringValue(PLAYER_QUEUE_LIST_SOURCE, value)

        }
    }

    class QueueList {
        companion object {
            private const val PLAYER_QUEUE_LIST_SOURCE = "PLAYER_QUEUE_LIST_SOURCE"
            private const val PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX = "PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX"
            private const val PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE = "PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE"

            var queueListSourceColumnIndex: String?
                get() = loadStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX, null)
                set(value) = saveStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_INDEX, value)

            var queueListSourceColumnValue: String?
                get() = loadStringValue(PLAYER_QUEUE_LIST_SOURCE_COLUMN_VALUE, null)
                set(value) = saveStringValue(PLAYER_QUEUE_LIST_SOURCE, value)

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
            private const val AUDIO_EFFECTS_REVERB_PROGRESS = "AUDIO_EFFECTS_REVERB_PROGRESS"
            private const val AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS = "AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS"

            var equalizerPresetName: String?
                get() = loadStringValue(AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME)
                set(value) = saveStringValue(AUDIO_EFFECTS_DEFAULT_EQUALIZER_PRESET_NAME, value)

            var equalizerCustomPresetValue: Any?
                get() = loadCustomObjectValue(AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS)
                set(value) = saveCustomObjectValue(AUDIO_EFFECTS_CUSTOM_EQUALIZER_BANDS_LEVELS, value)

            var bassBoostProgress: Int?
                get() = loadIntValue(AUDIO_EFFECTS_BASS_PROGRESS)
                set(value) = saveIntValue(AUDIO_EFFECTS_BASS_PROGRESS, value ?: 0)

            var visualizerProgress: Int?
                get() = loadIntValue(AUDIO_EFFECTS_VISUALIZER_PROGRESS)
                set(value) = saveIntValue(AUDIO_EFFECTS_VISUALIZER_PROGRESS, value ?: 0)

            var balanceProgress: Int?
                get() = loadIntValue(AUDIO_EFFECTS_BALANCE_PROGRESS)
                set(value) = saveIntValue(AUDIO_EFFECTS_BALANCE_PROGRESS, value ?: 0)

            var reverbProgress: Int?
                get() = loadIntValue(AUDIO_EFFECTS_REVERB_PROGRESS)
                set(value) = saveIntValue(AUDIO_EFFECTS_REVERB_PROGRESS, value ?: 0)

            var loudnessEnhancerProgress: Int?
                get() = loadIntValue(AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS)
                set(value) = saveIntValue(AUDIO_EFFECTS_LOUDNESS_ENHANCER_PROGRESS, value ?: 0)

            /**
             * States
             **/
            var equalizerState: Boolean?
                get() = loadBooleanValue(AUDIO_EFFECTS_ENABLE_EQUALIZER)
                set(value) = saveBooleanValue(AUDIO_EFFECTS_ENABLE_EQUALIZER, value ?: false)

            var toneState: Boolean?
                get() = loadBooleanValue(AUDIO_EFFECTS_ENABLE_TONE)
                set(value) = saveBooleanValue(AUDIO_EFFECTS_ENABLE_TONE, value ?: false)

            var balanceState: Boolean?
                get() = loadBooleanValue(AUDIO_EFFECTS_ENABLE_BALANCE)
                set(value) = saveBooleanValue(AUDIO_EFFECTS_ENABLE_BALANCE, value ?: false)

            var reverbState: Boolean?
                get() = loadBooleanValue(AUDIO_EFFECTS_ENABLE_REVERB)
                set(value) = saveBooleanValue(AUDIO_EFFECTS_ENABLE_REVERB, value ?: false)

            var loudnessEnhancerState: Boolean?
                get() = loadBooleanValue(AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER)
                set(value) = saveBooleanValue(AUDIO_EFFECTS_ENABLE_LOUDNESS_ENHANCER, value ?: false)
        }
    }

    class Settings {
        companion object {
            private const val SETTINGS_FIRST_TIME_LOADING = "SETTINGS_FIRST_TIME_LOADING"

            var firstTimeOpened: Boolean?
                get() = loadBooleanValue(SETTINGS_FIRST_TIME_LOADING)
                set(value) = saveBooleanValue(SETTINGS_FIRST_TIME_LOADING, value ?: false)
        }
    }

    class SortAndOrganize {
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

            fun load(sharedPrefsKey: String): SortOrganizeItemSP? {
                val tempGson = Gson()
                val tempItem: String? = INSTANCE?.preferences?.getString(sharedPrefsKey, null)
                val tempItemType = object : TypeToken<SortOrganizeItemSP>() {}.type
                return tempGson.fromJson<SortOrganizeItemSP>(tempItem, tempItemType)
            }
            fun save(sharedPrefsKey: String, sortOrganizeItemSP: SortOrganizeItemSP?) {
                val tempEditor: SharedPreferences.Editor = INSTANCE?.preferences?.edit() ?: return
                val tempGson = Gson()
                val tempJson: String = tempGson.toJson(sortOrganizeItemSP)
                tempEditor.putString(sharedPrefsKey, tempJson)
                tempEditor.apply()
            }
        }
    }
}