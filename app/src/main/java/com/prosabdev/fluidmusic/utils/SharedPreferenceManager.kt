package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.FolderSAF
import java.lang.reflect.Type

abstract class SharedPreferenceManager {
    companion object {
        fun loadSelectionFolderFromSAF(context: Context): List<FolderSAF>? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                AppCompatActivity.MODE_PRIVATE
            )
            val gson: Gson = Gson()
            val itemListJsonString: String? = sharedPreferences.getString("foldersselectionlistfromsaf", null)
            val itemType = object : TypeToken<List<FolderSAF>>() {}.type
            return gson.fromJson<List<FolderSAF>>(itemListJsonString, itemType)
        }
        fun saveSelectionFolderFromSAF(context: Context, folderList: List<FolderSAF>) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                AppCompatActivity.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            val gson: Gson = Gson()
            val json: String = gson.toJson(folderList)
            editor.putString("foldersselectionlistfromsaf", json)
            editor.apply()
            Log.i(ConstantValues.TAG, "Storage access folders saved !")
        }

        fun getRepeat(context: Context): Int {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getInt(ConstantValues.SHARED_PREFERENCES_REPEAT,
                PlaybackStateCompat.REPEAT_MODE_ONE
            )
        }

        fun setRepeat(context: Context, repeat: Int) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt(ConstantValues.SHARED_PREFERENCES_REPEAT, repeat)
            editor.apply()
        }
    }
}