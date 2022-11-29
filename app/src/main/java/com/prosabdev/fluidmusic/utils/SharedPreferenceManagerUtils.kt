package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.SleepTimerSP

abstract class SharedPreferenceManagerUtils {
    class Player {
        companion object {
            //Current song value
            fun loadCurrentPlayingSong(context: Context, sharedPreferences: SharedPreferences? = null): SongItem? {
                if(sharedPreferences != null){
                    val tempGson: Gson = Gson()
                    val tempItem: String? = sharedPreferences.getString(ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG, null)
                    val tempItemType = object : TypeToken<SongItem>() {}.type
                    Log.i(ConstantValues.TAG, "Current playing song loaded !")
                    return tempGson.fromJson<SongItem>(tempItem, tempItemType)
                }
                val tempGson: Gson = Gson()
                val tempSP: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG, null)
                val tempItemType = object : TypeToken<SongItem>() {}.type
                Log.i(ConstantValues.TAG, "Current playing song loaded !")
                return tempGson.fromJson<SongItem>(tempItem, tempItemType)
            }
            fun saveCurrentPlayingSong(context: Context, songItem: SongItem?) {
                val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson: Gson = Gson()
                val tempJson: String = tempGson.toJson(songItem)
                tempEditor.putString(ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG, tempJson)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Current playing song saved !")
            }

            //Sleep timer
            fun loadSleepTimer(context: Context, sharedPreferences: SharedPreferences? = null): SleepTimerSP? {
                if(sharedPreferences != null){
                    val tempGson: Gson = Gson()
                    val tempItem: String? = sharedPreferences.getString(ConstantValues.SHARED_PREFERENCES_SLEEP_TIMER, null)
                    val tempItemType = object : TypeToken<SleepTimerSP>() {}.type
                    Log.i(ConstantValues.TAG, "Sleep timer loaded !")
                    return tempGson.fromJson<SleepTimerSP>(tempItem, tempItemType)
                }
                val tempGson: Gson = Gson()
                val tempSP: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempItem: String? = tempSP.getString(ConstantValues.SHARED_PREFERENCES_SLEEP_TIMER, null)
                val tempItemType = object : TypeToken<SleepTimerSP>() {}.type
                Log.i(ConstantValues.TAG, "Sleep timer loaded !")
                return tempGson.fromJson<SleepTimerSP>(tempItem, tempItemType)
            }
            fun saveSleepTimer(context: Context, sleepTimerSP: SleepTimerSP?) {
                val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    AppCompatActivity.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
                val tempGson: Gson = Gson()
                val tempJson: String = tempGson.toJson(sleepTimerSP)
                tempEditor.putString(ConstantValues.SHARED_PREFERENCES_SLEEP_TIMER, tempJson)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Sleep timer saved !")
            }

            //Repeat
            fun loadRepeat(context: Context, sharedPreferences: SharedPreferences? = null): Int {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Current playing song seek position loaded !")
                    return sharedPreferences.getInt(
                        ConstantValues.SHARED_PREFERENCES_REPEAT,
                        PlaybackStateCompat.REPEAT_MODE_NONE
                    )
                }
                val tempSP: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Repeat loaded !")
                return tempSP.getInt(ConstantValues.SHARED_PREFERENCES_REPEAT, PlaybackStateCompat.REPEAT_MODE_NONE)
            }
            fun saveRepeat(context: Context, value: Int?) {
                val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putInt(ConstantValues.SHARED_PREFERENCES_REPEAT, value ?: PlaybackStateCompat.REPEAT_MODE_NONE)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Repeat saved !")
            }

            //Shuffle
            fun loadShuffle(context: Context, sharedPreferences: SharedPreferences? = null): Int {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Shuffle loaded !")
                    return sharedPreferences.getInt(
                        ConstantValues.SHARED_PREFERENCES_SHUFFLE,
                        PlaybackStateCompat.SHUFFLE_MODE_NONE
                    )
                }
                val tempSP: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Shuffle loaded !")
                return tempSP.getInt(ConstantValues.SHARED_PREFERENCES_SHUFFLE, PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
            fun saveShuffle(context: Context, value: Int?) {
                val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putInt(ConstantValues.SHARED_PREFERENCES_SHUFFLE, value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Shuffle saved !")
            }

            //Queue list source
            fun loadQueueListSource(context: Context, sharedPreferences: SharedPreferences? = null): String? {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Queue list source loaded !")
                    return sharedPreferences.getString(
                        ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
                        ConstantValues.EXPLORE_ALL_SONGS
                    )
                }
                val tempSP: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Queue list source loaded !")
                return tempSP.getString(
                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
                    ConstantValues.EXPLORE_ALL_SONGS
                )
            }
            fun saveQueueListSource(context: Context, value: String?) {
                val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putString(ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE, value)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Queue list source saved !")
            }

            //Queue list size
            fun loadPlayingProgressValue(context: Context, sharedPreferences: SharedPreferences? = null): Long {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Playing progress value loaded !")
                    return sharedPreferences.getLong(
                        ConstantValues.SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE,
                        0
                    )
                }
                val tempSP: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Playing progress value loaded !")
                return tempSP.getLong(
                    ConstantValues.SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE,
                    0
                )
            }
            fun savePlayingProgressValue(context: Context, value: Long?) {
                val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putLong(ConstantValues.SHARED_PREFERENCES_PLAYING_PROGRESS_VALUE, value ?: 0)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Playing progress value saved !")
            }

            //Queue list source value
            fun loadQueueListSourceValue(context: Context, sharedPreferences: SharedPreferences? = null): String? {
                if(sharedPreferences != null) {
                    Log.i(ConstantValues.TAG, "Queue list source value loaded !")
                    return sharedPreferences.getString(
                        ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
                        null
                    )
                }
                val tempSP: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                Log.i(ConstantValues.TAG, "Queue list source value loaded !")
                return tempSP.getString(
                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
                    null
                )
            }
            fun saveQueueListSourceValue(context: Context, value: String?) {
                val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val tempEditor: SharedPreferences.Editor = sharedPref.edit()
                tempEditor.putString(ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE, value)
                tempEditor.apply()
                Log.i(ConstantValues.TAG, "Queue list source value saved !")
            }
        }
    }

    class Settings {
        companion object {
            fun loadIsFirstTimeOpenApp(context: Context): Boolean {
                val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
                )
                return sharedPref.getBoolean("firsttimeopenapp", true)
            }
            fun saveIsFirstTimeOpenApp(context: Context, value: Boolean) {
                val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putBoolean("firsttimeopenapp", value)
                editor.apply()
            }
        }
    }
}