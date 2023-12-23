package com.prosabdev.common.utils

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
import com.prosabdev.common.components.Constants

object PermissionsManager {
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
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivity(intent)
        }
    }

    fun haveBluetoothPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun requestBluetoothPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                BLUETOOTH
            ),
            Constants.BLUETOOTH_PERMISSION_CODE
        )
    }
}