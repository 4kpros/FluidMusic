package com.prosabdev.fluidmusic.utils

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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

        fun getCurrentDateInMilli(): Long {
            return System.currentTimeMillis() / 1000
        }
    }

    open class SoftInputService(private val context: Context?, private val targetView: View?) :
        Runnable {
        private val handler: Handler = Handler(Looper.getMainLooper())
        override fun run() {
            if (context == null || targetView == null) {
                return
            }
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!targetView.isFocusable || !targetView.isFocusableInTouchMode) {
                Log.d(
                    TAG,
                    "focusable = " + targetView.isFocusable + ", focusableInTouchMode = " + targetView.isFocusableInTouchMode
                )
                return
            } else if (!targetView.requestFocus()) {
                Log.d(TAG, "Cannot focus on view")
                post()
            } else if (!imm.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT)) {
                Log.d(TAG, "Unable to show keyboard")
                post()
            }
        }
        fun show(delayBeforeShowInMilliSec : Long = 50) {
            INTERVAL_MS = delayBeforeShowInMilliSec
            handler.post(this)
        }
        private fun post() {
            handler.postDelayed(this, INTERVAL_MS)
        }

        companion object {
            private val TAG = SoftInputService::class.java.simpleName
            private var INTERVAL_MS : Long = 100
            fun hide(context: Context, windowToken: IBinder?) {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

}