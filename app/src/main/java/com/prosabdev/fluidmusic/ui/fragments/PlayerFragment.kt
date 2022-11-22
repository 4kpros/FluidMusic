package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
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
import kotlin.math.roundToLong


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mFragmentPlayerBinding: FragmentPlayerBinding

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private lateinit var mPlayerFragmentViewModel: PlayerFragmentViewModel
    private lateinit var mQueueMusicItemViewModel: QueueMusicItemViewModel
    private lateinit var mSongItemViewModel: SongItemViewModel

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null

    private var mPlayerMoreDialog: PlayerMoreDialog = PlayerMoreDialog()
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
        MainScope().launch {
            setupViewPagerAdapter()
            preloadWithEmptyItems()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
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
                    val position : Int = currentPlayingSongItem?.position?.toInt() ?: 0
//                    updateTextTitleSubtitleDurationUI(position)
//                    updateTextCurrentDurationUI(0)
//                    updateSliderCurrentDurationUI(0)
//                    updateBlurredBackgroundUI(position)
                }
            }
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
            ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE -> {
//                loadSongsForQueueListSource(
//                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE,
//                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SOURCE_VALUE,
//                    ConstantValues.SHARED_PREFERENCES_QUEUE_LIST_SIZE
//                )
            }
            ConstantValues.SHARED_PREFERENCES_REPEAT -> {
                val repeatValue = SharedPreferenceManager.loadRepeat(mContext, sharedPreferences)
//                updateRepeatUI(repeatValue)
            }
            ConstantValues.SHARED_PREFERENCES_SHUFFLE -> {
                val shuffleValue = SharedPreferenceManager.loadRepeat(mContext, sharedPreferences)
//                updateShuffleUI(shuffleValue)
            }
        }
    }

    private suspend fun preloadWithEmptyItems() {
        withContext(Dispatchers.IO){
            val currentPlayingSongItem: CurrentPlayingSongItem? = SharedPreferenceManager.loadCurrentPlayingSong(mContext)
            val queueListSource: String? = SharedPreferenceManager.loadQueueListSource(mContext)
            val queueListSourceValue: String? = SharedPreferenceManager.loadQueueListSourceValue(mContext)
            val queueListSize: Int = SharedPreferenceManager.loadQueueListSize(mContext)
            val repeat: Int = SharedPreferenceManager.loadRepeat(mContext)
            val shuffle: Int = SharedPreferenceManager.loadShuffle(mContext)

            var tempSongItem : SongItem? = null
            if(currentPlayingSongItem?.uri == null){
                tryToGetFirstSong()
            }else{
                tempSongItem = mSongItemViewModel.getSongInfoWithUri(currentPlayingSongItem.uri ?: "")
                if(tempSongItem?.uri == null){
                    tryToGetFirstSong()
                }else{
                    updateEmptyListUI(queueListSize)
                    updateBlurredBackgroundUIFromUri(tempSongItem.uri)
                    if(queueListSize > 0){
                        val tempQueueList: ArrayList<SongItem> = ArrayList()
                        for (i in 0 until queueListSize){
                            if(currentPlayingSongItem.position.toInt() == i){
                                val si : SongItem = SongItem()
                                si.id = currentPlayingSongItem.id
                                si.uri = currentPlayingSongItem.uri
                                si.title = currentPlayingSongItem.title
                                si.artist = currentPlayingSongItem.artist
                                si.fileName = currentPlayingSongItem.fileName
                                si.duration = currentPlayingSongItem.duration
                                si.uriTreeId = currentPlayingSongItem.uriTreeId
                                si.typeMime = currentPlayingSongItem.typeMime
                                tempQueueList.add(si)
                                MainScope().launch {
                                    mPlayerPagerAdapter?.submitList(tempQueueList)
                                    mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem((currentPlayingSongItem.position).toInt(), false)
                                }
                            }else{
                                tempQueueList.add(SongItem())
                            }
                        }
                        MainScope().launch {
                            mPlayerPagerAdapter?.submitList(tempQueueList)
                        }
                    }
                    loadSongsFromQueueListSource(queueListSource, queueListSourceValue, currentPlayingSongItem.position, false)
                }
            }

            updateRepeatUI(repeat)
            updateShuffleUI(shuffle)
        }
    }
    private suspend fun updateBlurredBackgroundUIFromUri(uriString: String?) {
        withContext(Dispatchers.Default){
            val tempUri : Uri = Uri.parse(uriString ?: "")
            CustomUILoaders.loadBlurredCovertArtFromSongUri(
                mContext,
                mFragmentPlayerBinding.blurredImageview,
                tempUri,
                100
            )
        }
    }
    private suspend fun tryToGetFirstSong() {
        withContext(Dispatchers.IO){
            var songItem : SongItem? = null
            songItem = mSongItemViewModel.getFirstSong()
            if(songItem?.uri == null){
                updateEmptyListUI(0)
                updateBlurredBackgroundUIFromUri(null)
                SharedPreferenceManager.saveCurrentPlayingSong(mContext, CurrentPlayingSongItem())
                SharedPreferenceManager.saveQueueListSource(mContext, ConstantValues.EXPLORE_ALL_SONGS)
                SharedPreferenceManager.saveQueueListSourceValue(mContext, "")
                SharedPreferenceManager.saveQueueListSize(mContext, 0)
            }else{
                updateEmptyListUI(1)
                updateBlurredBackgroundUIFromUri(songItem.uri)

                val tempQueueList: ArrayList<SongItem> = ArrayList()
                tempQueueList.add(songItem)
                MainScope().launch {
                    mPlayerPagerAdapter?.submitList(tempQueueList)
                    mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(0, false)
                }
                SharedPreferenceManager.saveQueueListSource(mContext, ConstantValues.EXPLORE_ALL_SONGS)
                SharedPreferenceManager.saveQueueListSourceValue(mContext, "")
                loadSongsFromQueueListSource(
                    ConstantValues.EXPLORE_ALL_SONGS,
                    "",
                    0,
                    false
                )
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

    private suspend fun loadSongsFromQueueListSource(
        queueListSource: String?,
        queueListSourceValue: String?,
        position : Long = 0,
        updateCurrentItem : Boolean = true
    ) {
        if(queueListSource == ConstantValues.EXPLORE_ALL_SONGS) {
            withContext(Dispatchers.IO){
                val result = mSongItemViewModel.getDirectlyAllSongsFromSource()
                MainScope().launch {
                    mPlayerPagerAdapter?.submitList(result)
                    updateEmptyListUI(result.size)
                    if(updateCurrentItem)
                        mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(position.toInt(), false)
                }
            }
        }
    }

    private fun updateEmptyListUI(size: Int) {
        if(size > 0){
            MainScope().launch {
                mFragmentPlayerBinding.linearRescanDeviceContainer.visibility = GONE
            }
        }else{
            MainScope().launch {
                mFragmentPlayerBinding.linearRescanDeviceContainer.visibility = VISIBLE
            }
        }
    }

    private fun updateRepeatUI(repeat: Int?) {
        MainScope().launch {
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
                    mFragmentPlayerBinding.buttonRepeat.alpha = 0.4f
                    mFragmentPlayerBinding.buttonRepeat.icon = ContextCompat.getDrawable(mContext, R.drawable.repeat)
                }
            }
        }
    }
    private fun updateShuffleUI(shuffle: Int?) {
        MainScope().launch {
            when (shuffle) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                    mFragmentPlayerBinding.buttonShuffle.alpha = 1.0f
                    mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(mContext, R.drawable.shuffle)
                }
                else -> {
                    mFragmentPlayerBinding.buttonShuffle.alpha = 0.4f
                    mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(mContext, R.drawable.shuffle)
                }
            }
        }
    }
    private fun updateSliderCurrentDurationUI(currentDuration : Long = 0) {
        MainScope().launch {
            mFragmentPlayerBinding.slider.value = currentDuration * 100.0f / (mPlayerPagerAdapter?.currentList?.get(mFragmentPlayerBinding.viewPagerPlayer.currentItem)?.duration ?: 0)
        }
    }
    private fun updateTextCurrentDurationUI(currentDuration : Long = 0) {
        MainScope().launch {
            mFragmentPlayerBinding.textDurationCurrent.text = CustomFormatters.formatSongDurationToString(currentDuration).toString()
        }
    }
    private fun updateTextTitleSubtitleDurationUI(position: Int) {
        if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= position)
            return
        MainScope().launch {
            mFragmentPlayerBinding.textTitle.text =
                if(mPlayerPagerAdapter?.currentList?.get(position)?.title != null )
                    mPlayerPagerAdapter?.currentList?.get(position)?.title
                else
                    mPlayerPagerAdapter?.currentList?.get(position)?.fileName

            mFragmentPlayerBinding.textArtist.text =
                if(mPlayerPagerAdapter?.currentList?.get(position)?.artist != null )
                    mPlayerPagerAdapter?.currentList?.get(position)?.artist
                else
                    mContext.getString(R.string.unknown_artist)

            mFragmentPlayerBinding.textDuration.text = CustomFormatters.formatSongDurationToString(mPlayerPagerAdapter?.currentList?.get(position)?.duration ?: 0).toString()
        }
    }
    private suspend fun updateBlurredBackgroundUI(position: Int) {
        if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= position)
            return
        withContext(Dispatchers.Default){
            val tempUri : Uri = Uri.parse(mPlayerPagerAdapter?.currentList?.get(position)?.uri ?: "")
            CustomUILoaders.loadBlurredCovertArtFromSongUri(
                mContext,
                mFragmentPlayerBinding.blurredImageview,
                tempUri,
                100
            )
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
    }

    private fun checkInteractions() {
        mFragmentPlayerBinding.viewPagerPlayer.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MainScope().launch {
                    onCurrentSongChangeFromViewpagerSwipe(position)
                }
            }
        })
        mFragmentPlayerBinding.slider.addOnSliderTouchListener(object : OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
                //
            }

            override fun onStopTrackingTouch(slider: Slider) {
                updateOnStopTrackingTouch(slider.value)
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
            mPlayerMoreDialog.show(childFragmentManager, PlayerMoreDialog.TAG)
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

    private fun updateOnStopTrackingTouch(value: Float) {
        val tempCurrentDuration : Long =
            ((value * (mPlayerPagerAdapter?.currentList?.get(mFragmentPlayerBinding.viewPagerPlayer.currentItem)?.duration
                ?: 0))/100).roundToLong()
        updateTextCurrentDurationUI(tempCurrentDuration)
    }

    private fun openMediaScannerActivity() {
        startActivity(Intent(mContext, MediaScannerSettingsActivity::class.java).apply {})
    }
    private fun onRepeat(){
    }
    private fun onShuffle(){
    }
    private fun onPrevPage(){
        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
            return
        val currentPosition :Int = mFragmentPlayerBinding.viewPagerPlayer.currentItem
        if(currentPosition > 0)
            onCurrentSongChangeFromButtonsNextPrev(currentPosition - 1)
    }
    private fun onNextPage(){
        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
            return
        val currentPosition :Int = mFragmentPlayerBinding.viewPagerPlayer.currentItem
        if((mPlayerPagerAdapter?.itemCount ?: 0) > 0 && currentPosition < (mPlayerPagerAdapter?.itemCount ?: 0) - 1)
            onCurrentSongChangeFromButtonsNextPrev(currentPosition + 1)
    }
    private fun onCurrentSongChangeFromButtonsNextPrev(position: Int) {
        MainScope().launch {
            mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(position, true)
            updateTextTitleSubtitleDurationUI(position)
            updateTextCurrentDurationUI(0)
            updateSliderCurrentDurationUI(0)
            updateBlurredBackgroundUI(position)

            castAndSaveCurrentIem(mPlayerPagerAdapter?.currentList?.get(position), position, false)
        }
    }
    private fun onPlayPause(){
        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
            return
        val tempPP : Boolean = !(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        mPlayerFragmentViewModel.setIsPlaying(tempPP)
    }
    private fun onCurrentSongChangeFromViewpagerSwipe(position: Int) {
        MainScope().launch {
            updateTextTitleSubtitleDurationUI(position)
            updateTextCurrentDurationUI(0)
            updateSliderCurrentDurationUI(0)
            updateBlurredBackgroundUI(position)

            castAndSaveCurrentIem(mPlayerPagerAdapter?.currentList?.get(position), position)
        }
    }
    private fun castAndSaveCurrentIem(songItem: SongItem?, position: Int, saveSeekDuration: Boolean = true) {
        val currentPlayingSong = CurrentPlayingSongItem()
        if(songItem == null){
            SharedPreferenceManager.saveCurrentPlayingSong(mContext, currentPlayingSong)
            return
        }
        currentPlayingSong.id = songItem.id
        currentPlayingSong.position = position.toLong()
        currentPlayingSong.fileName = songItem.fileName
        currentPlayingSong.fileName = songItem.fileName
        currentPlayingSong.uri = songItem.uri
        if(saveSeekDuration){
            var sliderValue : Float = mFragmentPlayerBinding.slider.value
            if(sliderValue.isNaN())
                sliderValue = 0.0f
            currentPlayingSong.currentSeekDuration = (sliderValue * songItem.duration).roundToLong()
        }
        currentPlayingSong.artist = songItem.artist
        currentPlayingSong.duration = songItem.duration
        currentPlayingSong.title = songItem.title
        currentPlayingSong.typeMime = songItem.typeMime
        currentPlayingSong.uriTreeId = songItem.uriTreeId
        SharedPreferenceManager.saveCurrentPlayingSong(mContext, currentPlayingSong)
        SharedPreferenceManager.saveQueueListSize(mContext, mPlayerPagerAdapter?.itemCount ?: 0)
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