package com.prosabdev.common.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri

object IntentActionsManager {
    suspend fun shareBitmapImage(ctx : Context?, bitmap: Bitmap?, textDescription : String) {
        if(ctx == null || bitmap == null) return

        val uri : Uri = ContentProviders.saveScreeShotImageBitmap(ctx, bitmap) ?: return

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.putExtra(Intent.EXTRA_TEXT, textDescription)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.type = "image/*"
        try {
            ctx.startActivity(Intent.createChooser(shareIntent,"Share To: "))
        }finally {
            //
        }
    }

    fun shareSongFile(ctx : Context?, uri : Uri?, textDescription : String){
        if(ctx == null) return

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.putExtra(Intent.EXTRA_TEXT, textDescription)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.type = "audio/*"
        try {
            ctx.startActivity(Intent.createChooser(shareIntent,"Share To: "))
        }finally {
            //
        }
    }
}