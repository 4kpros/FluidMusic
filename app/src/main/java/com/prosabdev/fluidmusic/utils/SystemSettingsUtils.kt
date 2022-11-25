package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast

abstract class SystemSettingsUtils {

    companion object {
        fun setRingtone(ctx: Context, uri: Uri?, fileName : String? = null, showToast :Boolean = false): Boolean {
            if (uri == null) return false

            try {
                RingtoneManager.setActualDefaultRingtoneUri(
                    ctx, RingtoneManager.TYPE_RINGTONE,
                    uri
                )
                if(showToast){
                    Toast.makeText(ctx, "${fileName} set as ringtone", Toast.LENGTH_SHORT).show()
                }
            }catch (error : Throwable){
                error.printStackTrace()
            }

            return false
        }
    }

}