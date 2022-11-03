package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.content.SharedPreferences
import com.prosabdev.fluidmusic.R

abstract class SharedPreferenceManager {
    companion object {
        fun getRequestBroadcastLoading(context: Context): Boolean {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean(
                ConstantValues.SHARED_PREFERENCES_BROADCAST,
                false
            )
        }

        fun setRequestBroadcastLoading(context: Context, isLoaded: Boolean) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean(
                ConstantValues.SHARED_PREFERENCES_BROADCAST,
                isLoaded
            )
            editor.apply()
        }


        fun getShuffle(context: Context): Boolean {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean(ConstantValues.SHARED_PREFERENCES_SHUFFLE, false)
        }

        fun setShuffle(context: Context, shuffle: Boolean) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean(ConstantValues.SHARED_PREFERENCES_SHUFFLE, shuffle)
            editor.apply()
        }

        fun getRepeat(context: Context): Int {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            return sharedPref.getInt(ConstantValues.SHARED_PREFERENCES_REPEAT, ConstantValues.REPEAT_NONE)
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