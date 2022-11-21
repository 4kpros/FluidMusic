package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayerBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongItem
import com.prosabdev.fluidmusic.roomdatabase.bus.DatabaseAccessApplication
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicDialog
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.QueueMusicItemViewModel
import com.prosabdev.fluidmusic.viewmodels.views.explore.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.views.explore.SongItemViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModelFactory
import kotlinx.coroutines.*
import kotlin.math.abs


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mFragmentPlayerBinding: FragmentPlayerBinding

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private lateinit var mPlayerFragmentViewModel: PlayerFragmentViewModel
    private lateinit var mQueueMusicItemViewModel: QueueMusicItemViewModel
    private lateinit var mSongItemViewModel: SongItemViewModel

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null

    private var mPlayerMoreDialog: PlayerMoreDialog? = null
    private var mQueueMusicDialog: QueueMusicDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mFragmentPlayerBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_player,container,false)
        val view = mFragmentPlayerBinding.root

        initViews()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            setupViewPagerAdapter()
            loadPlayingLastSession()
            checkInteractions()
            observeLiveData()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i(ConstantValues.TAG, "On shared preferences save !!")
        when (key) {
            ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG -> {
                val currentPlayingSongItem: CurrentPlayingSongItem? = SharedPreferenceManager.loadCurrentPlayingSong(mContext, sharedPreferences)
                MainScope().launch {
                    updateCurrentPlayingSongUI(currentPlayingSongItem)
                }
            }
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE -> {
                loadSongsForQueueListSource(
                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE
                )
            }
            ConstantValues.SHARED_PREFERENCES_REPEAT -> {
                val repeatValue = SharedPreferenceManager.loadRepeat(mContext, sharedPreferences)
                updateRepeatUI(repeatValue)
            }
            ConstantValues.SHARED_PREFERENCES_SHUFFLE -> {
                val shuffleValue = SharedPreferenceManager.loadRepeat(mContext, sharedPreferences)
                updateShuffleUI(shuffleValue)
            }
        }
    }

    private fun loadSongsForQueueListSource(
        queueListSource: String,
        queueListSourceValue: String,
        queueListSize: String,
    ) {
        if(queueListSource == ConstantValues.EXPLORE_ALL_SONGS){
            lifecycleScope.launch(Dispatchers.IO){
                val tempSongList = mSongItemViewModel.getDirectlyAllSongsFromSource()
                mPlayerPagerAdapter?.submitList(tempSongList)
            }
        }
    }

    private suspend fun loadPlayingLastSession(sharedPreferences: SharedPreferences? = null) {
        withContext(Dispatchers.Default){
            val currentPlayingSongItem: CurrentPlayingSongItem? = SharedPreferenceManager.loadCurrentPlayingSong(mContext, sharedPreferences)
            val queueListSource: String? = SharedPreferenceManager.loadQueueListSource(mContext, sharedPreferences)
            val queueListSourceValue: String? = SharedPreferenceManager.loadQueueListSourceValue(mContext, sharedPreferences)
            val queueListSize: Int = SharedPreferenceManager.loadQueueListSize(mContext, sharedPreferences)
            val repeat: Int = SharedPreferenceManager.loadRepeat(mContext, sharedPreferences)
            val shuffle: Int = SharedPreferenceManager.loadShuffle(mContext, sharedPreferences)

            updateCurrentPlayingSongUI(currentPlayingSongItem)
            preloadEmptySongsForViewPager(queueListSize, currentPlayingSongItem)
            updateRepeatUI(repeat)
            updateShuffleUI(shuffle)
            loadSongsFromQueueListSource(queueListSource, queueListSourceValue)
        }
    }

    private fun preloadEmptySongsForViewPager(queueListSize: Int, currentPlayingSongItem: CurrentPlayingSongItem?) {
        if(queueListSize <= 0 || currentPlayingSongItem == null || currentPlayingSongItem.position == null)
            return
        CoroutineScope(Dispatchers.Default).launch {
            val queueList : ArrayList<SongItem> = ArrayList()
            for (i in 0 until queueListSize){
                queueList.add(SongItem())
            }
            MainScope().launch {
                mPlayerPagerAdapter?.submitList(queueList)
                mFragmentPlayerBinding.viewPagerPlayer.currentItem = currentPlayingSongItem.position.toInt()
            }
        }
    }

    private fun loadSongsFromQueueListSource(queueListSource: String?, queueListSourceValue: String?) {
        if(queueListSource == ConstantValues.EXPLORE_ALL_SONGS) {
            lifecycleScope.launch(Dispatchers.IO){
                mSongItemViewModel.getAllSongs().collect(){

                }
            }
        }else if(queueListSource == ConstantValues.EXPLORE_ALL_SONGS) {
            lifecycleScope.launch(Dispatchers.IO){
//                mQueueMusicItemViewModel.getAllQueueMusicList()
            }
        }
    }
    private fun updateCurrentPlayingSongUI(
        currentPlayingSongItem: CurrentPlayingSongItem?
    ) {
        toggleNoSongFoundUI(currentPlayingSongItem)
        if (currentPlayingSongItem != null && currentPlayingSongItem.position != null){
            mFragmentPlayerBinding.textTitle.text =
                if(currentPlayingSongItem.title != null )
                    currentPlayingSongItem.title
                else
                    currentPlayingSongItem.fileName
            mFragmentPlayerBinding.textArtist.text =
                if(currentPlayingSongItem.artist != null )
                    currentPlayingSongItem.artist
                else
                    mContext.getString(R.string.unknown_artist)

            mFragmentPlayerBinding.textDuration.text = CustomFormatters.formatSongDurationToString(currentPlayingSongItem.duration)
            mFragmentPlayerBinding.textDurationCurrent.text = CustomFormatters.formatSongDurationToString(currentPlayingSongItem.currentSeekDuration)
        }
    }

    private fun toggleNoSongFoundUI(currentPlayingSongItem: CurrentPlayingSongItem?) {
        MainScope().launch {
            if(currentPlayingSongItem != null && currentPlayingSongItem.position != null){
                mFragmentPlayerBinding.linearRescanDeviceContainer.visibility = GONE
            }else{
                mFragmentPlayerBinding.linearRescanDeviceContainer.visibility = VISIBLE
            }
        }
    }

    private fun setupViewPagerAdapter() {
        mPlayerPagerAdapter =
            PlayerPageAdapter(mContext, object : PlayerPageAdapter.OnItemClickListener {
                override fun onButtonLyricsClicked(position: Int) {
                    Toast.makeText(context, "onButtonLyricsClicked", Toast.LENGTH_SHORT).show()
                }

                override fun onButtonFullscreenClicked(position: Int) {
                    Toast.makeText(context, "onButtonFullscreenClicked", Toast.LENGTH_SHORT).show()
                }

            })
        mFragmentPlayerBinding.viewPagerPlayer.adapter = mPlayerPagerAdapter
        mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(0, false)
        mFragmentPlayerBinding.viewPagerPlayer.clipToPadding = false
        mFragmentPlayerBinding.viewPagerPlayer.clipChildren = false
        mFragmentPlayerBinding.viewPagerPlayer.offscreenPageLimit = 5
        mFragmentPlayerBinding.viewPagerPlayer.getChildAt(0)?.overScrollMode = View.OVER_SCROLL_NEVER
        mFragmentPlayerBinding.viewPagerPlayer.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                CustomMathComputations.fadeWithPageOffset(mFragmentPlayerBinding.blurredImageview, positionOffset)
            }
        })
        transformPageScale(mFragmentPlayerBinding.viewPagerPlayer)
    }
    private fun transformPageScale(viewPager: ViewPager2?) {
        if(viewPager == null)
            return
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(5))
        compositePageTransformer.addTransformer { page, position ->
            val normalizedPosition = abs(abs(position) - 1)
            page.alpha = normalizedPosition
            page.scaleX = normalizedPosition / 2 + 0.5f
            page.scaleY = normalizedPosition / 2 + 0.5f
            page.translationX = position * -100
        }
        viewPager.setPadding(0, 0, 0, 0)
        viewPager.setPageTransformer(compositePageTransformer)
    }

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getIsPlaying().observe(mActivity as LifecycleOwner
        ) { updatePlayerButtonsUI() }
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(mActivity as LifecycleOwner
        ) { progressValue -> updateProgress(progressValue) }
    }
    private fun updateProgress(progressValue: Long?) {
//        mFragmentPlayerBinding.linearProgressBarContainer
    }
    private fun updateRepeatUI(repeat: Int?) {
        when (repeat) {
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                mFragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                mFragmentPlayerBinding.buttonRepeat.icon = ContextCompat.getDrawable(mContext, R.drawable.repeat)
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                mFragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                mFragmentPlayerBinding.buttonRepeat.icon = ContextCompat.getDrawable(mContext, R.drawable.repeat_one)
            }
            else -> {
                mFragmentPlayerBinding.buttonRepeat.alpha = 0.5f
                mFragmentPlayerBinding.buttonRepeat.icon = ContextCompat.getDrawable(mContext, R.drawable.repeat)
            }
        }
    }
    private fun updateShuffleUI(shuffle: Int?) {
        when (shuffle) {
            PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                mFragmentPlayerBinding.buttonShuffle.alpha = 1.0f
                mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(mContext, R.drawable.shuffle)
            }
            else -> {
                mFragmentPlayerBinding.buttonShuffle.alpha = 0.5f
                mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(mContext, R.drawable.shuffle)
            }
        }
    }
    private fun updatePlayerButtonsUI() {
        if(mPlayerFragmentViewModel.getIsPlaying().value == true){
            mFragmentPlayerBinding.buttonPlayPause.icon = ContextCompat.getDrawable(mContext, R.drawable.pause_circle)
        }else{
            mFragmentPlayerBinding.buttonPlayPause.icon = ContextCompat.getDrawable(mContext, R.drawable.play_circle)
        }
    }
    private suspend fun updatePlayerUI(position: Int) {
        if(position == mFragmentPlayerBinding.viewPagerPlayer.currentItem)
            return
        val  tempCurrentItem : SongItem? = mPlayerPagerAdapter?.currentList?.get(position)
        MainScope().launch {
            if(tempCurrentItem != null){
                mFragmentPlayerBinding.textTitle.text = if(tempCurrentItem.title!!.isNotEmpty()) tempCurrentItem.title else tempCurrentItem.fileName //Set song title

                mFragmentPlayerBinding.textArtist.text = if(tempCurrentItem.artist!!.isNotEmpty()) tempCurrentItem.artist else mContext.getString(R.string.unknown_artist)

                mFragmentPlayerBinding.textDurationCurrent.text = CustomFormatters.formatSongDurationToString(0)

                mFragmentPlayerBinding.textDuration.text = CustomFormatters.formatSongDurationToString(tempCurrentItem.duration)

                mFragmentPlayerBinding.slider.value = 0.0f
            }
        }
        withContext(Dispatchers.IO){
            val tempBinary : ByteArray? = if(tempCurrentItem != null ) tempCurrentItem.covertArt?.binaryData else null
            CustomUILoaders.loadCovertArtFromBinaryData(mContext, mFragmentPlayerBinding.blurredImageview, tempBinary, 500)
        }
    }

    private fun checkInteractions() {
        mFragmentPlayerBinding.viewPagerPlayer.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MainScope().launch {
                    updatePlayerUI(position)
                }
            }
        })
        mFragmentPlayerBinding.buttonPlayPause.setOnClickListener {
            onPlayPause()
        }
        mFragmentPlayerBinding.buttonSkipNext.setOnClickListener {
            onNextPage()
        }
        mFragmentPlayerBinding.buttonSkipPrev.setOnClickListener {
            onPrevPage()
        }
        mFragmentPlayerBinding.buttonShuffle.setOnClickListener {
            onShuffle()
        }
        mFragmentPlayerBinding.buttonRepeat.setOnClickListener {
            onRepeat()
        }
        mFragmentPlayerBinding.buttonMore.setOnClickListener {
            if (mPlayerMoreDialog == null)
                mPlayerMoreDialog = PlayerMoreDialog()
            mPlayerMoreDialog?.show(childFragmentManager, PlayerMoreDialog.TAG)
        }
        mFragmentPlayerBinding.buttonQueueMusic.setOnClickListener {
            mQueueMusicDialog = QueueMusicDialog()
            mQueueMusicDialog?.show(childFragmentManager, QueueMusicDialog.TAG)
        }
        mFragmentPlayerBinding.buttonEqualizer.setOnClickListener {
            mActivity.supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<EqualizerFragment>(R.id.main_activity_fragment_container)
                addToBackStack(null)
            }
        }
        mFragmentPlayerBinding.buttonArrowDown.setOnClickListener {
            //
        }
        mFragmentPlayerBinding.buttonRescanDevice.setOnClickListener {
            openMediaScannerActivity()
        }
    }
    private fun openMediaScannerActivity() {
        startActivity(Intent(mContext, MediaScannerSettingsActivity::class.java).apply {})
    }

    private fun onPlayPause(){
        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
            return
        val tempPP : Boolean = !(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        mPlayerFragmentViewModel.setIsPlaying(tempPP)
    }
    private fun onNextPage(){
//        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
//            return
//        val tempCS :Int = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
//        if((mPlayerPagerAdapter?.itemCount ?: 0) > 0 && tempCS < (mPlayerPagerAdapter?.itemCount ?: 0) - 1)
//            mPlayerFragmentViewModel.setCurrentSong(tempCS + 1)
    }
    private fun onPrevPage(){
//        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
//            return
//        val tempCS :Int = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
//        if(tempCS > 0)
//            mPlayerFragmentViewModel.setCurrentSong(tempCS - 1)
    }
    private fun onShuffle(){
    }
    private fun onRepeat(){
    }

    private fun initViews() {
        mPlayerFragmentViewModel = PlayerFragmentViewModelFactory().create(PlayerFragmentViewModel::class.java)
        mSongItemViewModel = SongItemViewModelFactory(
            (activity?.application as DatabaseAccessApplication).database.songItemDao()
        ).create(SongItemViewModel::class.java)

        mFragmentPlayerBinding.blurredImageview.layout(0,0,0,0)
        mFragmentPlayerBinding.textTitle.isSelected = true
        mFragmentPlayerBinding.textArtist.isSelected = true

        CustomViewModifiers.updateTopViewInsets(mFragmentPlayerBinding.linearRescanDeviceContainer)
        CustomViewModifiers.updateTopViewInsets(mFragmentPlayerBinding.linearViewpager)
        CustomViewModifiers.updateBottomViewInsets(mFragmentPlayerBinding.linearControls)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}