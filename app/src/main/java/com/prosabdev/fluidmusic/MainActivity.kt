package com.prosabdev.fluidmusic

import android.content.ComponentName
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.color.DynamicColors
import com.google.android.material.navigation.NavigationView
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.services.MediaPlaybackService
import com.prosabdev.fluidmusic.ui.fragments.MainFragment
import com.prosabdev.fluidmusic.utils.AudioFileInfoExtractor
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(){

    private var mNavigationView : NavigationView? = null
    private var mDrawerLayout : DrawerLayout? = null
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by viewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by viewModels()

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

            // Finish building the UI
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
        val mediaController = MediaControllerCompat.getMediaController(this@MainActivity)
        mediaController.transportControls.prepare()

        // Display the initial state
        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        mediaController.registerCallback(mControllerCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.main_activity_fragment_container)
            }
        }

        setContentView(R.layout.activity_main)


        runBlocking {
            initViews(savedInstanceState)
            createMediaBrowserService()
            checkInteractions()
            observeLiveData()
        }
    }

    private fun createMediaBrowserService() {
        // Create MediaBrowserServiceCompat
        mMediaBrowser = MediaBrowserCompat(
            applicationContext,
            ComponentName(applicationContext, MediaPlaybackService::class.java),
            mConnectionCallbacks,
            null // optional Bundle
        )
    }

    private fun observeLiveData() {
        //Listen for queue list updated
        mPlayerFragmentViewModel.getSongList().observe(this as LifecycleOwner, object : Observer<ArrayList<SongItem>>{
            override fun onChanged(songList: ArrayList<SongItem>?) {
                updateQueueList(songList)
            }
        })
        //Listen for current song updated
        mPlayerFragmentViewModel.getCurrentSong().observe(this as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(currentSong: Int?) {
                MainScope().launch {
                    updateCurrentPlayingSong(currentSong)
                }
            }
        })
        //Listen for source of queue list updated
        mPlayerFragmentViewModel.getSourceOfQueueList().observe(this as LifecycleOwner, object : Observer<String> {
            override fun onChanged(sourceOf: String?) {
                updateDataFromSource(sourceOf)
            }
        })
    }

    private fun updateDataFromSource(sourceOf: String?) {
        //
    }

    private suspend fun updateCurrentPlayingSong(currentSong: Int?)  = coroutineScope{
        if ((mPlayerFragmentViewModel.getSongList().value?.size ?: 0) > 0) {
            val bundle = createQueueListBundle(currentSong ?: 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaController?.transportControls?.playFromUri(
                    Uri.parse(mPlayerFragmentViewModel.getSongList().value?.get(currentSong ?: 0)?.absolutePath),
                    bundle
                )
            }
        }

    }

    private fun createQueueListBundle(currentSong: Int): Bundle {
        val bundle = Bundle()
        //Get queue list
        val queueList : ArrayList<String> = ArrayList()
        for (i in 0 until (mPlayerFragmentViewModel.getSongList().value?.size ?: 0)){
            val tempSong : String? = mPlayerFragmentViewModel.getSongList().value?.get(i)?.absolutePath
            if(tempSong != null && tempSong.isNotEmpty())
                queueList.add(tempSong)
        }
        //Setup song title
        val tempTitle = if(mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.title.isNullOrBlank())
            mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.fileName
        else
            mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.title
        //Get bitmap image from binary data
        val temBitmap : Bitmap? = AudioFileInfoExtractor.getBitmapAudioArtwork(
            this,
            mPlayerFragmentViewModel.getSongList().value?.get(currentSong)?.covertArt?.binaryData,
            100,
            100
            )
        //Create new media meta data for current playing song
        val tempMediaMetadataCompat = MediaMetadataCompat.Builder()
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, temBitmap)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mPlayerFragmentViewModel.getSongList().value?.get(currentSong)!!.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mPlayerFragmentViewModel.getSongList().value?.get(currentSong)!!.album)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, tempTitle)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mPlayerFragmentViewModel.getSongList().value?.get(currentSong)!!.duration)
            .build()

        //Setup bundle
        bundle.putParcelable(ConstantValues.BUNDLE_CURRENT_SONG_META_DATA, tempMediaMetadataCompat)
        bundle.putStringArrayList(ConstantValues.BUNDLE_QUEUE_LIST, queueList)
        bundle.putString(ConstantValues.BUNDLE_SOURCE_FROM, mPlayerFragmentViewModel.getSourceOfQueueList().value)
        bundle.putString(ConstantValues.BUNDLE_SOURCE_FROM_VALUE, mPlayerFragmentViewModel.getSourceOfQueueListValue().value)
        bundle.putInt(ConstantValues.BUNDLE_SHUFFLE_VALUE, mPlayerFragmentViewModel.getShuffle().value ?: 0)
        bundle.putInt(ConstantValues.BUNDLE_REPEAT_VALUE, mPlayerFragmentViewModel.getRepeat().value ?: 0)
        bundle.putInt(ConstantValues.BUNDLE_CURRENT_SONG_ID, currentSong)
        return bundle
    }

    private fun updateQueueList(songList: ArrayList<SongItem>?) {
        //
    }

    public override fun onStart() {
        super.onStart()
        mMediaBrowser?.connect()
    }

    public override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    public override fun onStop() {
        super.onStop()
        // (see "stay in sync with the MediaSession")
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mControllerCallback)
        mMediaBrowser?.disconnect()
    }

    private fun checkInteractions() {
        mMainExploreFragmentViewModel.mActionBarState.observe(this, Observer { item ->
            if(item){
                mDrawerLayout?.open()
            }
        })
    }

    //Initialize views
    private fun initViews(savedInstanceState : Bundle?) {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.navigation_view)

        mNavigationView?.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            mDrawerLayout?.close()
            true
        }
    }
}