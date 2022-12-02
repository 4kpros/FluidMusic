package com.prosabdev.fluidmusic

import android.content.ComponentName
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.color.DynamicColors
import com.prosabdev.fluidmusic.databinding.ActivityMainBinding
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.SleepTimerSP
import com.prosabdev.fluidmusic.service.MediaPlaybackService
import com.prosabdev.fluidmusic.ui.fragments.EqualizerFragment
import com.prosabdev.fluidmusic.ui.fragments.MainFragment
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.ImageLoadersUtils
import com.prosabdev.fluidmusic.utils.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.models.explore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@BuildCompat.PrereleaseSdkCheck class MainActivity : AppCompatActivity(){

    private lateinit var mActivityMainBinding: ActivityMainBinding

    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by viewModels()

    private val mAllSongsFragmentViewModel: AllSongsFragmentViewModel by viewModels()
    private val mAlbumsFragmentViewModel: AlbumsFragmentViewModel by viewModels()
    private val mAlbumArtistsFragmentViewModel: AlbumArtistsFragmentViewModel by viewModels()
    private val mArtistsFragmentViewModel: ArtistsFragmentViewModel by viewModels()
    private val mFoldersFragmentViewModel: FoldersFragmentViewModel by viewModels()
    private val mGenresFragmentViewModel: GenresFragmentViewModel by viewModels()
    private val mComposersFragmentViewModel: ComposersFragmentViewModel by viewModels()
    private val mYearsFragmentViewModel: YearsFragmentViewModel by viewModels()

    private val mSongItemViewModel: SongItemViewModel by viewModels()
    private val mAlbumItemViewModel: AlbumItemViewModel by viewModels()
    private val mAlbumArtistItemViewModel: AlbumArtistItemViewModel by viewModels()
    private val mArtistItemViewModel: ArtistItemViewModel by viewModels()
    private val mFolderItemViewModel: FolderItemViewModel by viewModels()
    private val mGenreItemViewModel: GenreItemViewModel by viewModels()
    private val mComposerItemViewModel: ComposerItemViewModel by viewModels()
    private val mYearItemViewModel: YearItemViewModel by viewModels()

    private val mMainFragment = MainFragment.newInstance()
    private val mEqualizerFragment = EqualizerFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupFragments()
        MainScope().launch {
            initViews()
            loadLastPlayerSession()
            requestListenData()
            observeLiveData()
            setupAudioSettings()
            createMediaBrowserService()
        }
    }

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getShowEqualizerFragmentCounter().observe(this){
            if(it > 0){
                showEqualizerFragment()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
//        if (newConfig.uiMode == UI_MODE_NIGHT_NO) {
//            setTheme(R.style.MainTheme)
//        } else {
//            setTheme(R.style.MainThemeDark)
//        }
    }

    private fun requestListenData() {
        mAllSongsFragmentViewModel.listenAllData(mSongItemViewModel, this)
        mAlbumsFragmentViewModel.listenAllData(mAlbumItemViewModel, this)
        mAlbumArtistsFragmentViewModel.listenAllData(mAlbumArtistItemViewModel, this)
        mArtistsFragmentViewModel.listenAllData(mArtistItemViewModel, this)
        mFoldersFragmentViewModel.listenAllData(mFolderItemViewModel, this)
        mGenresFragmentViewModel.listenAllData(mGenreItemViewModel, this)
        mComposersFragmentViewModel.listenAllData(mComposerItemViewModel, this)
        mYearsFragmentViewModel.listenAllData(mYearItemViewModel, this)
    }

    override fun onDestroy() {
        ImageLoadersUtils.stopAllJobsWorkers()
        saveCurrentPlayingSession()
        super.onDestroy()
    }

    private fun saveCurrentPlayingSession() {
        SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(
            applicationContext,
            mPlayerFragmentViewModel.getCurrentPlayingSong().value
        )
        SharedPreferenceManagerUtils.Player.savePlayingProgressValue(
            applicationContext,
            mPlayerFragmentViewModel.getPlayingProgressValue().value
        )
        SharedPreferenceManagerUtils.Player.saveQueueListSource(
            applicationContext,
            mPlayerFragmentViewModel.getQueueListSource().value
        )
        SharedPreferenceManagerUtils.Player.saveQueueListSourceValue(
            applicationContext,
            mPlayerFragmentViewModel.getSourceOfQueueListValue().value
        )
        SharedPreferenceManagerUtils.Player.saveRepeat(
            applicationContext,
            mPlayerFragmentViewModel.getRepeat().value
        )
        SharedPreferenceManagerUtils.Player.saveShuffle(
            applicationContext,
            mPlayerFragmentViewModel.getShuffle().value
        )
        SharedPreferenceManagerUtils.Player.saveSleepTimer(
            applicationContext,
            mPlayerFragmentViewModel.getSleepTimer().value
        )
    }
    private fun createMediaBrowserService() {
        mMediaBrowser = MediaBrowserCompat(
            applicationContext,
            ComponentName(applicationContext, MediaPlaybackService::class.java),
            mConnectionCallbacks,
            null
        )
    }
    private fun setupAudioSettings() {
        volumeControlStream = AudioManager.STREAM_MUSIC
    }
    private suspend fun loadLastPlayerSession() {
        withContext(Dispatchers.IO){
            val songItem: SongItem? = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(applicationContext)
            val progressValue: Long = SharedPreferenceManagerUtils.Player.loadPlayingProgressValue(applicationContext)
            val queueListSource: String? = SharedPreferenceManagerUtils.Player.loadQueueListSource(applicationContext)
            val queueListSourceValue: String? = SharedPreferenceManagerUtils.Player.loadQueueListSourceValue(applicationContext)
            val repeat: Int = SharedPreferenceManagerUtils.Player.loadRepeat(applicationContext)
            val shuffle: Int = SharedPreferenceManagerUtils.Player.loadShuffle(applicationContext)
            val sleepTimer: SleepTimerSP? = SharedPreferenceManagerUtils.Player.loadSleepTimer(applicationContext)

            if(songItem?.uri == null){
                tryToGetFirstSong()
            }else{
                val tempSongItem : SongItem? =
                    mSongItemViewModel.getAtUri(songItem.uri ?: "")
                if(tempSongItem?.uri == null){
                    tryToGetFirstSong()
                }else{
                    mPlayerFragmentViewModel.setCurrentPlayingSong(tempSongItem)
                    mPlayerFragmentViewModel.setPlayingProgressValue(progressValue)
                    mPlayerFragmentViewModel.setIsPlaying(false)
                    mPlayerFragmentViewModel.setRepeat(repeat)
                    mPlayerFragmentViewModel.setShuffle(shuffle)
                    mPlayerFragmentViewModel.setQueueListSource(queueListSource ?: ConstantValues.EXPLORE_ALL_SONGS)
                    mPlayerFragmentViewModel.setQueueListSourceValue(queueListSourceValue)
                    mPlayerFragmentViewModel.setSleepTimer(sleepTimer)
                    mPlayerFragmentViewModel.setSleepTimerStateStarted(false)
                }
            }
        }
    }
    private suspend fun tryToGetFirstSong() {
        withContext(Dispatchers.IO){
            mSongItemViewModel.getFirstSong().apply {
                SharedPreferenceManagerUtils.Player.saveQueueListSource(applicationContext, ConstantValues.EXPLORE_ALL_SONGS)
                SharedPreferenceManagerUtils.Player.saveQueueListSourceValue(applicationContext, "")
                SharedPreferenceManagerUtils.Player.saveRepeat(applicationContext, PlaybackStateCompat.REPEAT_MODE_NONE)
                SharedPreferenceManagerUtils.Player.saveShuffle(applicationContext, PlaybackStateCompat.SHUFFLE_MODE_NONE)
                SharedPreferenceManagerUtils.Player.saveSleepTimer(applicationContext, null)
                SharedPreferenceManagerUtils.Player.savePlayingProgressValue(applicationContext, 0)

                if(this@apply?.uri == null){
                    mPlayerFragmentViewModel.setCurrentPlayingSong(null)
                    SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(applicationContext, null)
                }else{
                    mPlayerFragmentViewModel.setCurrentPlayingSong(this@apply)
                    SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(applicationContext, this@apply)
                }
            }
        }
    }

    private fun setupFragments() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_activity_fragment_container, mMainFragment)
        }
    }
    private fun showEqualizerFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.main_activity_fragment_container, mEqualizerFragment)
            addToBackStack(EqualizerFragment.TAG)
        }
    }

    private fun initViews(){
    }


    private var mMediaBrowser: MediaBrowserCompat? = null
    private var mConnectionCallbacks: ConnectionCallback = object : ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()
            Log.i(ConstantValues.TAG, "ConnectionCallback onConnected")
            mMediaBrowser?.sessionToken.also { token ->
                val mediaController = MediaControllerCompat(
                    this@MainActivity.applicationContext, // Context
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

        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        mediaController.registerCallback(mControllerCallback)
    }

    override fun onPause() {
        super.onPause()
        ImageLoadersUtils.stopAllJobsWorkers()
    }

    public override fun onStart() {
        super.onStart()
        ImageLoadersUtils.initializeJobWorker()
        mMediaBrowser?.connect()
    }
    public override fun onResume() {
        super.onResume()
        ImageLoadersUtils.initializeJobWorker()
        setupAudioSettings()
    }

    public override fun onStop() {
        super.onStop()
        ImageLoadersUtils.stopAllJobsWorkers()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mControllerCallback)
        mMediaBrowser?.disconnect()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
