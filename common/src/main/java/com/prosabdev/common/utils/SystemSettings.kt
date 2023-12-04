package com.prosabdev.common.utils

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

object SystemSettings {

    /**
     * Method used to set song as ringtone. Return true if success and otherwise false
     */
    fun setRingtone(ctx: Context, uri: Uri?, fileName : String? = null, showToast :Boolean = false): Boolean {
        if (uri == null) return false

        try {
            RingtoneManager.setActualDefaultRingtoneUri(
                ctx, RingtoneManager.TYPE_RINGTONE,
                uri
            )
            if(showToast){
                Toast.makeText(ctx, "$fileName set as ringtone", Toast.LENGTH_SHORT).show()
            }
            return true
        }catch (error : Throwable){
            error.printStackTrace()
        }

        return false
    }

    /**
     * Return current date in milli seconds
     */
    fun getCurrentDateInMillis(): Long {
        return System.currentTimeMillis() / 1000
    }

    /**
     * Input service class
     */
    class SoftInputService(private val context: Context?, private val targetView: View?) :
        Runnable
    {
        private val mHandler: Handler = Handler(Looper.getMainLooper())

        //Delay time var to show keyboard
        private var mDelayTimeInMillis : Long = 50

        override fun run() {
            if (context == null || targetView == null) {
                return
            }
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!targetView.isFocusable || !targetView.isFocusableInTouchMode) {
                return
            } else if (!targetView.requestFocus()) {
                post()
            } else if (!inputMethodManager.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT)) {
                post()
            }
        }
        private fun post() {
            mHandler.postDelayed(this, mDelayTimeInMillis)
        }

        /**
         * Show keyboard with delay time. This delay time is important in order to show keyboard. Delay time with 0 won't show the keyboard.
         */
        fun show(delayBeforeShowInMilliSec : Long = 50) {
            mDelayTimeInMillis = delayBeforeShowInMilliSec
            mHandler.post(this)
        }

        /**
         * Hide keyboard
         */
        fun hide(context: Context, windowToken: IBinder?) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

}