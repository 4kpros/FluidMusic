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
            if(folderList.isNotEmpty())
                saveHaveFoldersSAF(context, true)
            else
                saveHaveFoldersSAF(context, false)
            Log.i(ConstantValues.TAG, "Storage access folders saved !")
        }

        fun loadHaveFoldersSAF(context: Context): Boolean {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean("havefolderssaf", true)
        }
        private fun saveHaveFoldersSAF(context: Context, b: Boolean) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("havefolderssaf", b)
            editor.apply()
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