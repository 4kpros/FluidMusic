package com.prosabdev.fluidmusic

import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.databinding.ActivityMainBinding
import com.prosabdev.fluidmusic.services.MediaPlaybackService
import com.prosabdev.fluidmusic.ui.activities.SettingsActivity
import com.prosabdev.fluidmusic.ui.fragments.*
import com.prosabdev.fluidmusic.utils.ConstantValues


@BuildCompat.PrereleaseSdkCheck class MainActivity : AppCompatActivity(){

    private lateinit var mActivityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
        attachFragments()
        checkInteractions()
        createMediaBrowserService()
    }

    private fun attachFragments() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_activity_fragment_container, MainFragment.newInstance())
        }
    }

    private fun createMediaBrowserService() {
        mMediaBrowser = MediaBrowserCompat(
            applicationContext,
            ComponentName(applicationContext, MediaPlaybackService::class.java),
            mConnectionCallbacks,
            null // optional Bundle
        )
    }

    private fun checkInteractions() {
        mActivityMainBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if(mActivityMainBinding.navigationView.checkedItem?.itemId != menuItem.itemId){
                when (menuItem.itemId) {
                    R.id.music_library -> {
                        mActivityMainBinding.drawerLayout.close()
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.main_fragment_container, MusicLibraryFragment.newInstance())
                        }
                    }
                    R.id.device_explorer -> {
                        mActivityMainBinding.drawerLayout.close()
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.main_fragment_container, DeviceExplorerFragment.newInstance())
                        }
                    }
                    R.id.playlists -> {
                        mActivityMainBinding.drawerLayout.close()
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.main_fragment_container, PlaylistsFragment.newInstance())
                        }
                    }
                    R.id.favorites -> {
                        mActivityMainBinding.drawerLayout.close()
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.main_fragment_container, FavoritesFragment.newInstance())
                        }
                    }
                }
            }
            when (menuItem.itemId) {
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java).apply {})
                }
            }
            menuItem.isChecked = true

            true
        }
    }
    private fun updateDrawerMenu() {
        when (supportFragmentManager.findFragmentById(R.id.main_fragment_container)) {
            is MusicLibraryFragment -> {
                mActivityMainBinding.navigationView.setCheckedItem(R.id.music_library)
            }
            is DeviceExplorerFragment -> {
                mActivityMainBinding.navigationView.setCheckedItem(R.id.device_explorer)
            }
            is PlaylistsFragment -> {
                mActivityMainBinding.navigationView.setCheckedItem(R.id.playlists)
            }
            is FavoritesFragment -> {
                mActivityMainBinding.navigationView.setCheckedItem(R.id.favorites)
            }
        }
    }

    private fun initViews(){
        mActivityMainBinding.navigationView.setCheckedItem(R.id.music_library)
    }

    private var mMediaBrowser: MediaBrowserCompat? = null
    private var mConnectionCallbacks: ConnectionCallback = object : ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnected")
            mMediaBrowser?.sessionToken.also { token ->
                val mediaController = MediaControllerCompat(
                    this@MainActivity, // Context
                    token!!
                )
                MediaControllerCompat.setMediaController(this@MainActivity, mediaController)
            }
            buildTransportControls()
        }
        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnectionSuspended")
        }
        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnectionFailed")
        }
    }
    private var mControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.i(ConstantValues.TAG, "MediaControllerCompat onMetadataChanged : $metadata")
        }
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.i(ConstantValues.TAG, "MediaControllerCompat onPlaybackStateChanged : $state")
        }
    }

    private fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this)
        mediaController.transportControls.prepare()

//        val metadata = mediaController.metadata
//        val pbState = mediaController.playbackState

        mediaController.registerCallback(mControllerCallback)
    }



//    private fun createQueueListBundle(currentSong: Int): Bundle {
//        val bundle = Bundle()
//        //Get queue list
//        val queueList : ArrayList<String> = ArrayList()
//        for (i in 0 until (mPlayerFragmentViewModel.getSongList().value?.size ?: 0)){
//            val tempSong : String? = mPlayerFragmentViewModel.getSongList().value?.get(i)?.absolutePath
//            if(tempSong != null && tempSong.isNotEmpty())
//                queueList.add(tempSong)
//        }
//        //Setup song title
//        val tempTitle = if(mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.title.isNullOrBlank())
//            mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.fileName
//        else
//            mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.title
//        //Get bitmap image from binary data
//        val temBitmap : Bitmap? = AudioFileInfoExtractor.getBitmapAudioArtwork(
//            mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.covertArt?.binaryData,
//            100,
//            100
//            )
//        //Create new media meta data for current playing song
//        val tempMediaMetadataCompat = MediaMetadataCompat.Builder()
//            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, temBitmap)
//            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mPlayerFragmentViewModel.getSongList().value?.get(currentSong)!!.artist)
//            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mPlayerFragmentViewModel.getSongList().value?.get(currentSong)!!.album)
//            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, tempTitle)
//            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mPlayerFragmentViewModel.getSongList().value?.get(currentSong)!!.duration)
//            .build()
//
//        //Setup bundle
//        bundle.putParcelable(ConstantValues.BUNDLE_CURRENT_SONG_META_DATA, tempMediaMetadataCompat)
//        bundle.putStringArrayList(ConstantValues.BUNDLE_QUEUE_LIST, queueList)
//        bundle.putString(ConstantValues.BUNDLE_SOURCE_FROM, mPlayerFragmentViewModel.getSourceOfQueueList().value)
//        bundle.putString(ConstantValues.BUNDLE_SOURCE_FROM_VALUE, mPlayerFragmentViewModel.getSourceOfQueueListValue().value)
//        bundle.putInt(ConstantValues.BUNDLE_SHUFFLE_VALUE, mPlayerFragmentViewModel.getShuffle().value ?: 0)
//        bundle.putInt(ConstantValues.BUNDLE_REPEAT_VALUE, mPlayerFragmentViewModel.getRepeat().value ?: 0)
//        bundle.putInt(ConstantValues.BUNDLE_CURRENT_SONG_ID, currentSong)
//        return bundle
//    }

    public override fun onStart() {
        super.onStart()
        mMediaBrowser?.connect()
    }
    public override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
        updateDrawerMenu()
    }

    public override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mControllerCallback)
        mMediaBrowser?.disconnect()
    }
}