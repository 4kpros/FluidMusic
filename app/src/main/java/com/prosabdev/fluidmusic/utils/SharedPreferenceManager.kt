package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongItem

abstract class SharedPreferenceManager {
    companion object {
        //Current song value
        fun loadCurrentPlayingSong(context: Context, sharedPreferences: SharedPreferences? = null): CurrentPlayingSongItem? {
            if(sharedPreferences != null){
                val tempGson: Gson = Gson()
                val tempItemListJsonString: String? = sharedPreferences.getString(ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG, null)
                val tempItemType = object : TypeToken<CurrentPlayingSongItem>() {}.type
                Log.i(ConstantValues.TAG, "CurrentPlayingSong loaded !")
                return tempGson.fromJson<CurrentPlayingSongItem>(tempItemListJsonString, tempItemType)
            }
            val tempGson: Gson = Gson()
            val tempSP: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                AppCompatActivity.MODE_PRIVATE
            )
            val tempItemListJsonString: String? = tempSP.getString(ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG, null)
            val tempItemType = object : TypeToken<CurrentPlayingSongItem>() {}.type
            Log.i(ConstantValues.TAG, "Current playing song loaded !")
            return tempGson.fromJson<CurrentPlayingSongItem>(tempItemListJsonString, tempItemType)
        }
        fun saveCurrentPlayingSong(context: Context, currentPlayingSongItem: CurrentPlayingSongItem) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                AppCompatActivity.MODE_PRIVATE
            )
            val tempEditor: SharedPreferences.Editor = sharedPreferences.edit()
            val tempGson: Gson = Gson()
            val tempJson: String = tempGson.toJson(currentPlayingSongItem)
            tempEditor.putString(ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG, tempJson)
            tempEditor.apply()
            Log.i(ConstantValues.TAG, "Current playing song saved !")
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
        fun saveRepeat(context: Context, value: Int) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val tempEditor: SharedPreferences.Editor = sharedPref.edit()
            tempEditor.putInt(ConstantValues.SHARED_PREFERENCES_REPEAT, value)
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
        fun saveShuffle(context: Context, value: Int) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val tempEditor: SharedPreferences.Editor = sharedPref.edit()
            tempEditor.putInt(ConstantValues.SHARED_PREFERENCES_SHUFFLE, value)
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
        fun saveQueueListSource(context: Context, value: String) {
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
        fun loadQueueListSize(context: Context, sharedPreferences: SharedPreferences? = null): Int {
            if(sharedPreferences != null) {
                Log.i(ConstantValues.TAG, "Queue list size loaded !")
                return sharedPreferences.getInt(
                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE,
                    0
                )
            }
            val tempSP: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            Log.i(ConstantValues.TAG, "Queue list size loaded !")
            return tempSP.getInt(
                ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE,
                0
            )
        }
        fun saveQueueListSize(context: Context, value: Int) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val tempEditor: SharedPreferences.Editor = sharedPref.edit()
            tempEditor.putInt(ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE, value)
            tempEditor.apply()
            Log.i(ConstantValues.TAG, "Queue list size saved !")
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
        fun saveQueueListSourceValue(context: Context, value: String) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val tempEditor: SharedPreferences.Editor = sharedPref.edit()
            tempEditor.putString(ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE, value)
            tempEditor.apply()
            Log.i(ConstantValues.TAG, "Queue list source value saved !")
        }


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