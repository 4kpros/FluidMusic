package com.prosabdev.fluidmusic

import android.content.ComponentName
import android.media.AudioManager
import android.net.Uri
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
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel

class MainActivity : AppCompatActivity(){

    private var mNavigationView : NavigationView? = null
    private var mDrawerLayout : DrawerLayout? = null
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by viewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by viewModels()

    private lateinit var mMediaBrowser: MediaBrowserCompat
    private var mConnectionCallbacks: ConnectionCallback = object : ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnected")
            mMediaBrowser.sessionToken.also { token ->
                val mediaController = MediaControllerCompat(
                    this@MainActivity, // Context
                    token
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

        setContentView(R.layout.activity_main)

        // Create MediaBrowserServiceCompat
        mMediaBrowser = MediaBrowserCompat(
            applicationContext,
            ComponentName(applicationContext, MediaPlaybackService::class.java),
            mConnectionCallbacks,
            null // optional Bundle
        )

        initViews(savedInstanceState)
        observeLiveData()
        checkInteractions()
    }

    private fun observeLiveData() {
        //Listen for queue list updated
        mPlayerFragmentViewModel.getQueueList().observe(this as LifecycleOwner, object : Observer<ArrayList<SongItem>>{
            override fun onChanged(songList: ArrayList<SongItem>?) {
                updateQueueList(songList)
            }
        })
        //Listen for current song updated
        mPlayerFragmentViewModel.getCurrentSong().observe(this as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(currentSong: Int?) {
                updateCurrentPlayingSong(currentSong)
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

    private fun updateCurrentPlayingSong(currentSong: Int?) {
        if ((mPlayerFragmentViewModel.getQueueList().value?.size ?: 0) <= 0)
            return
        Log.i(ConstantValues.TAG, "updateCurrentPlayingSong")
        Log.i(ConstantValues.TAG, "Queue list = ${mPlayerFragmentViewModel.getQueueList().value?.size ?: 0}")
        Log.i(ConstantValues.TAG, "Current song = ${currentSong ?: 0} with path = ${mPlayerFragmentViewModel.getQueueList().value?.get(currentSong ?: 0)?.absolutePath}")

        val bundle = createQueueListBundle(currentSong ?: 0)
        mediaController?.transportControls?.playFromMediaId(
            mPlayerFragmentViewModel.getQueueList().value?.get(currentSong ?: 0)?.absolutePath,
            bundle
        )
    }

    private fun createQueueListBundle(currentSong: Int): Bundle {
        val bundle = Bundle()
        val queueList : ArrayList<MediaSessionCompat.QueueItem> = ArrayList()
        for (i in 0 until (mPlayerFragmentViewModel.getQueueList().value?.size ?: 0)){
            if(mPlayerFragmentViewModel.getQueueList().value?.get(i) != null && mPlayerFragmentViewModel.getQueueList().value?.get(i)!!.absolutePath?.isNotEmpty() == true){
                val description = MediaDescriptionCompat.Builder()
                    .setMediaId(mPlayerFragmentViewModel.getQueueList().value?.get(i)!!.absolutePath ?: "")
                    .setExtras(bundle)
                    .setTitle(mPlayerFragmentViewModel.getQueueList().value?.get(i)!!.title ?: "")
                    .setSubtitle(mPlayerFragmentViewModel.getQueueList().value?.get(i)!!.artist ?: "")
                    .setMediaUri(Uri.parse(mPlayerFragmentViewModel.getQueueList().value?.get(i)!!.absolutePath ?: ""))
                    .setIconBitmap(mPlayerFragmentViewModel.getQueueList().value?.get(i)!!.covertArt)
                    .build()
                val queItem : MediaSessionCompat.QueueItem = MediaSessionCompat.QueueItem(
                    description,
                    i.toLong()
                )
                queueList.add(queItem)
            }
        }
        val tempMediaMetadataCompat = MediaMetadataCompat.Builder()
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, mPlayerFragmentViewModel.getQueueList().value?.get(currentSong)!!.covertArt)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mPlayerFragmentViewModel.getQueueList().value?.get(currentSong)!!.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mPlayerFragmentViewModel.getQueueList().value?.get(currentSong)!!.album)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mPlayerFragmentViewModel.getQueueList().value?.get(currentSong)!!.title)
            .build()
        val queueList2 : ArrayList<String> = ArrayList()
        bundle.putParcelable(ConstantValues.BUNDLE_CURRENT_SONG_META_DATA, tempMediaMetadataCompat)
//        bundle.putParcelableArray(ConstantValues.BUNDLE_QUEUE_LIST, queueList.toTypedArray())
        bundle.putString(ConstantValues.BUNDLE_SOURCE_FROM, mPlayerFragmentViewModel.getSourceOfQueueList().value)
        bundle.putString(ConstantValues.BUNDLE_CURRENT_SONG_ID, mPlayerFragmentViewModel.getSourceOfQueueListValue().value)
        bundle.putInt(ConstantValues.BUNDLE_SHUFFLE_VALUE, mPlayerFragmentViewModel.getShuffle().value ?: 0)
        bundle.putInt(ConstantValues.BUNDLE_REPEAT_VALUE, mPlayerFragmentViewModel.getRepeat().value ?: 0)
        bundle.putInt(ConstantValues.BUNDLE_CURRENT_SONG_ID, mPlayerFragmentViewModel.getCurrentSong().value ?: 0)
        return bundle
    }

    private fun updateQueueList(songList: ArrayList<SongItem>?) {
        //
    }

    public override fun onStart() {
        super.onStart()
        mMediaBrowser.connect()
    }

    public override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    public override fun onStop() {
        super.onStop()
        // (see "stay in sync with the MediaSession")
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mControllerCallback)
        mMediaBrowser.disconnect()
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
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.main_activity_fragment_container)
            }
        }

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