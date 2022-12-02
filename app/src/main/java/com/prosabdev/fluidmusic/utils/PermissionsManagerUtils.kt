package com.prosabdev.fluidmusic.utils

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


abstract class PermissionsManagerUtils {

    companion object {
        //Check if user have storage access
        fun haveStoragePermissions(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ContextCompat.checkSelfPermission(
                    context,
                    WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    context,
                    READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && (
                        ContextCompat.checkSelfPermission(
                            context,
                            MANAGE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED)
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context,
                            READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun haveBluetoothPermissions(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun haveRecordAudioPermissions(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestStoragePermission(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        MANAGE_EXTERNAL_STORAGE
                    ),
                    ConstantValues.STORAGE_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                    ),
                    ConstantValues.STORAGE_PERMISSION_CODE
                )
            }
        }
        fun haveWriteSystemSettingsPermission(ctx: Context): Boolean {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                Settings.System.canWrite(ctx)
            }else{
                ContextCompat.checkSelfPermission(
                    ctx,
                    WRITE_SETTINGS
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        fun requestWriteSystemSettingsPermission(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivity(intent)
            } else {
                //
            }
        }

        fun requestBluetoothPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    BLUETOOTH
                ),
                ConstantValues.BLUETOOTH_PERMISSION_CODE
            )
        }

        fun requestAudioRecordPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    RECORD_AUDIO,
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE
                ),
                ConstantValues.AUDIO_RECORD_PERMISSION_CODE
            )
        }
    }
}