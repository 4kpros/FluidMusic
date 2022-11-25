package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentPlayerBinding
import com.prosabdev.fluidmusic.models.explore.SongItem
import com.prosabdev.fluidmusic.models.sharedpreference.CurrentPlayingSongSP
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreBottomSheetDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicDialog
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.ModelsViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToLong


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mFragmentPlayerBinding: FragmentPlayerBinding


    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
//    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()
//    private val mQueueMusicItemViewModel: QueueMusicItemViewModel by activityViewModels()

    private lateinit var mSongItemViewModel: SongItemViewModel

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null

    private var mPlayerMoreBottomSheetDialog: PlayerMoreBottomSheetDialog? = null
    private var mQueueMusicDialog: QueueMusicDialog? = null

    private var mOldViewpagerPosition: Int = 0
    private var mCurrentDuration: Long = 0
    private var mDuration: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
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
        val ctx : Context = this@PlayerFragment.context ?: return
        when (key) {
            ConstantValues.SHARED_PREFERENCES_CURRENT_PLAYING_SONG -> {
//                val currentPlayingSongItem: CurrentPlayingSongItem? = SharedPreferenceManager.Player.loadCurrentPlayingSong(ctx, sharedPreferences)

                MainScope().launch {
//                    val position : Int = currentPlayingSongItem?.position?.toInt() ?: 0
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
                val repeatValue = SharedPreferenceManagerUtils.Player.loadRepeat(ctx, sharedPreferences)
//                updateRepeatUI(repeatValue)
            }
            ConstantValues.SHARED_PREFERENCES_SHUFFLE -> {
                val shuffleValue = SharedPreferenceManagerUtils.Player.loadRepeat(ctx, sharedPreferences)
//                updateShuffleUI(shuffleValue)
            }
        }
    }

    private suspend fun preloadWithEmptyItems() {
        withContext(Dispatchers.IO){
            val ctx : Context = this@PlayerFragment.context ?: return@withContext
            val currentPlayingSongSP: CurrentPlayingSongSP? = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(ctx)
            val queueListSource: String? = SharedPreferenceManagerUtils.Player.loadQueueListSource(ctx)
            val queueListSourceValue: String? = SharedPreferenceManagerUtils.Player.loadQueueListSourceValue(ctx)
            val queueListSize: Int = SharedPreferenceManagerUtils.Player.loadQueueListSize(ctx)
            val repeat: Int = SharedPreferenceManagerUtils.Player.loadRepeat(ctx)
            val shuffle: Int = SharedPreferenceManagerUtils.Player.loadShuffle(ctx)

            updateRepeatUI(repeat)
            updateShuffleUI(shuffle)

            var tempSongItem : SongItem? = null
            if(currentPlayingSongSP?.uri == null){
                tryToGetFirstSong()
            }else{
                tempSongItem = mSongItemViewModel.getAtUri(currentPlayingSongSP.uri ?: "")
                if(tempSongItem?.uri == null){
                    tryToGetFirstSong()
                }else{
                    mCurrentDuration = currentPlayingSongSP.currentSeekDuration
                    mDuration = currentPlayingSongSP.duration
                    updateEmptyListUI(queueListSize)
                    updateBlurredBackgroundUIFromUri(tempSongItem.uri)

                    if(queueListSize > 0){
                        val tempQueueList: ArrayList<SongItem> = ArrayList()
                        for (i in 0 until queueListSize){
                            if(currentPlayingSongSP.position.toInt() == i){
                                val si : SongItem = SongItem()
                                si.id = currentPlayingSongSP.id
                                si.uri = currentPlayingSongSP.uri
                                si.title = currentPlayingSongSP.title
                                si.artist = currentPlayingSongSP.artist
                                si.fileName = currentPlayingSongSP.fileName
                                si.duration = currentPlayingSongSP.duration
                                si.uriTreeId = currentPlayingSongSP.uriTreeId
                                si.typeMime = currentPlayingSongSP.typeMime
                                tempQueueList.add(si)
                                MainScope().launch {
                                    mOldViewpagerPosition = currentPlayingSongSP.position.toInt()
                                    mPlayerPagerAdapter?.submitList(tempQueueList)
                                    mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(mOldViewpagerPosition, false)
                                }
                                launch{
                                    loadSongsFromQueueListSource(queueListSource, queueListSourceValue)
                                }
                            }else{
                                tempQueueList.add(SongItem())
                            }
                        }
                        MainScope().launch {
                            mPlayerPagerAdapter?.submitList(tempQueueList)
                        }
                    }
                }
            }
        }
    }
    private suspend fun updateBlurredBackgroundUIFromUri(uriString: String?) {
        withContext(Dispatchers.Default){
            val ctx : Context = this@PlayerFragment.context ?: return@withContext
            val tempUri : Uri = Uri.parse(uriString ?: "")
            ImageLoadersUtils.loadBlurredCovertArtFromSongUri(
                ctx,
                mFragmentPlayerBinding.blurredImageview,
                tempUri,
                100
            )
        }
    }
    private suspend fun tryToGetFirstSong() {
        withContext(Dispatchers.IO){
            mSongItemViewModel.getFirstSong().apply {
                val ctx : Context = this@PlayerFragment.context ?: return@apply
                mCurrentDuration = 0

                if(this?.uri == null){
                    updateEmptyListUI(0)
                    updateBlurredBackgroundUIFromUri(null)
                    SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(ctx, CurrentPlayingSongSP())
                    SharedPreferenceManagerUtils.Player.saveQueueListSource(ctx, ConstantValues.EXPLORE_ALL_SONGS)
                    SharedPreferenceManagerUtils.Player.saveQueueListSourceValue(ctx, "")
                    SharedPreferenceManagerUtils.Player.saveQueueListSize(ctx, 0)
                }else{
                    updateEmptyListUI(1)
                    updateBlurredBackgroundUIFromUri(this.uri)

                    mDuration = this.duration

                    val tempQueueList: ArrayList<SongItem> = ArrayList()
                    tempQueueList.add(this)
                    MainScope().launch {
                        mPlayerPagerAdapter?.submitList(tempQueueList)
                    }
                    loadSongsFromQueueListSource(
                        ConstantValues.EXPLORE_ALL_SONGS,
                        ""
                    )
                    SharedPreferenceManagerUtils.Player.saveQueueListSource(ctx, ConstantValues.EXPLORE_ALL_SONGS)
                    SharedPreferenceManagerUtils.Player.saveQueueListSourceValue(ctx, "")
                }
            }
        }
    }

    private suspend fun loadSongsFromQueueListSource(
        queueListSource: String?,
        queueListSourceValue: String?
    ) {
        MainScope().launch {
            if(queueListSource == ConstantValues.EXPLORE_ALL_SONGS) {
                mSongItemViewModel.getAll()?.observe(this@PlayerFragment as LifecycleOwner){
                    mPlayerPagerAdapter?.submitList(it)
                    return@observe
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
            val ctx : Context = this@PlayerFragment.context ?: return@launch
            when (repeat) {
                PlaybackStateCompat.REPEAT_MODE_ALL -> {
                    mFragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                    mFragmentPlayerBinding.buttonRepeat.icon = ContextCompat.getDrawable(ctx, R.drawable.repeat)
                }
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    mFragmentPlayerBinding.buttonRepeat.alpha = 1.0f
                    mFragmentPlayerBinding.buttonRepeat.icon = ContextCompat.getDrawable(ctx, R.drawable.repeat_one)
                }
                else -> {
                    mFragmentPlayerBinding.buttonRepeat.alpha = 0.4f
                    mFragmentPlayerBinding.buttonRepeat.icon = ContextCompat.getDrawable(ctx, R.drawable.repeat)
                }
            }
        }
    }
    private fun updateShuffleUI(shuffle: Int?) {
        MainScope().launch {
            val ctx : Context = this@PlayerFragment.context ?: return@launch
            when (shuffle) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                    mFragmentPlayerBinding.buttonShuffle.alpha = 1.0f
                    mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(ctx, R.drawable.shuffle)
                }
                else -> {
                    mFragmentPlayerBinding.buttonShuffle.alpha = 0.4f
                    mFragmentPlayerBinding.buttonShuffle.icon = ContextCompat.getDrawable(ctx, R.drawable.shuffle)
                }
            }
        }
    }
    private fun updateSliderCurrentDurationUI() {
        val scaledDuration : Float = if(mDuration > mCurrentDuration) mCurrentDuration * 100.0f / mDuration else 0.0f
        MainScope().launch {
            mFragmentPlayerBinding.slider.value = scaledDuration
        }
    }
    private fun updateTextCurrentDurationUI() {
        MainScope().launch {
            mFragmentPlayerBinding.textDurationCurrent.text = FormattersUtils.formatSongDurationToString(mCurrentDuration).toString()
        }
    }
    private fun updateTextTitleSubtitleDurationUI(position: Int) {
        if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= position)
            return
        MainScope().launch {
            val ctx : Context = this@PlayerFragment.context ?: return@launch
            mFragmentPlayerBinding.textTitle.text =
                if(mPlayerPagerAdapter?.currentList?.get(position)?.title != null )
                    mPlayerPagerAdapter?.currentList?.get(position)?.title
                else
                    mPlayerPagerAdapter?.currentList?.get(position)?.fileName

            mFragmentPlayerBinding.textArtist.text =
                if(mPlayerPagerAdapter?.currentList?.get(position)?.artist != null )
                    mPlayerPagerAdapter?.currentList?.get(position)?.artist
                else
                    ctx.getString(R.string.unknown_artist)

            mFragmentPlayerBinding.textDuration.text = FormattersUtils.formatSongDurationToString(mPlayerPagerAdapter?.currentList?.get(position)?.duration ?: 0).toString()
        }
    }
    private suspend fun updateBlurredBackgroundUI(position: Int) {
        if ((mPlayerPagerAdapter?.currentList?.size ?: 0) <= position)
            return
        withContext(Dispatchers.Default){
            val ctx : Context = this@PlayerFragment.context ?: return@withContext
            val tempUri : Uri = Uri.parse(mPlayerPagerAdapter?.currentList?.get(position)?.uri ?: "")
            ImageLoadersUtils.loadBlurredCovertArtFromSongUri(
                ctx,
                mFragmentPlayerBinding.blurredImageview,
                tempUri,
                100
            )
        }
    }

    private fun setupViewPagerAdapter() {
        mPlayerPagerAdapter =
            PlayerPageAdapter(this.requireContext(), object : PlayerPageAdapter.OnItemClickListener {
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
        mFragmentPlayerBinding.viewPagerPlayer.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                MathComputaionsUtils.fadeWithPageOffset(mFragmentPlayerBinding.blurredImageview, positionOffset)
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onViewpagerPageChanged(position)
            }
        })
        mFragmentPlayerBinding.slider.addOnSliderTouchListener(object : OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
                //
            }
            override fun onStopTrackingTouch(slider: Slider) {
                MainScope().launch {
                    updateOnStopTrackingTouch(slider.value)
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
            showMoreOptionsDialog()
        }
        mFragmentPlayerBinding.buttonQueueMusic.setOnClickListener {
            mQueueMusicDialog = QueueMusicDialog()
            mQueueMusicDialog?.show(childFragmentManager, QueueMusicDialog.TAG)
        }
        mFragmentPlayerBinding.buttonEqualizer.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
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

    private fun showMoreOptionsDialog() {
        if(mPlayerMoreBottomSheetDialog?.isVisible == false)
            mPlayerMoreBottomSheetDialog?.show(childFragmentManager, PlayerMoreBottomSheetDialog.TAG)
    }

    private suspend fun updateOnStopTrackingTouch(value: Float) {
        val ctx : Context = this@PlayerFragment.context ?: return
        val tempCurrentDuration : Long =
            ((value * (mPlayerPagerAdapter?.currentList?.get(mFragmentPlayerBinding.viewPagerPlayer.currentItem)?.duration
                ?: 0))/100).roundToLong()
        mCurrentDuration = tempCurrentDuration
        updateTextCurrentDurationUI()
        withContext(Dispatchers.IO){
            val currentPlaying : CurrentPlayingSongSP = SharedPreferenceManagerUtils.Player.loadCurrentPlayingSong(ctx) ?: CurrentPlayingSongSP()
            currentPlaying.currentSeekDuration = tempCurrentDuration
            if(currentPlaying.uri != null)
                SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(ctx, currentPlaying)
        }
    }

    private fun openMediaScannerActivity() {
        val tempActivity : Context = this@PlayerFragment.activity ?: return
        startActivity(Intent(tempActivity, MediaScannerSettingsActivity::class.java).apply {})
    }
    private fun onRepeat(){
    }
    private fun onShuffle(){
    }
    private fun onPrevPage(){
        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
            return
        val currentPosition :Int = mFragmentPlayerBinding.viewPagerPlayer.currentItem
        if(currentPosition > 0){
            mCurrentDuration = 0
            changeCurrentSongTo(currentPosition - 1)
        }
    }
    private fun onNextPage(){
        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
            return
        val currentPosition :Int = mFragmentPlayerBinding.viewPagerPlayer.currentItem
        if(currentPosition > 0 && currentPosition < (mPlayerPagerAdapter?.itemCount ?: 0)){
            mCurrentDuration = 0
            changeCurrentSongTo(currentPosition + 1)
        }
    }
    private fun onPlayPause(){
        if((mPlayerPagerAdapter?.itemCount ?: 0) <= 0)
            return
    }
    private fun onViewpagerPageChanged(position: Int) {
        if(mOldViewpagerPosition != position) {
            mOldViewpagerPosition = position
            mCurrentDuration = 0
        }
        changeCurrentSongTo(position)
    }
    private fun changeCurrentSongTo(position: Int) {
        if(position >= 0 && position >= (mPlayerPagerAdapter?.currentList?.size ?: 0))
            return
        MainScope().launch {
            mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(position, true)
            mDuration = mPlayerPagerAdapter?.currentList?.get(position)?.duration ?: 0
            updateTextTitleSubtitleDurationUI(position)
            updateTextCurrentDurationUI()
            updateSliderCurrentDurationUI()
            updateBlurredBackgroundUI(position)

            castAndSaveCurrentIem(mPlayerPagerAdapter?.currentList?.get(position), position)
        }
    }
    private fun castAndSaveCurrentIem(songItem: SongItem?, position: Int) {
        val currentPlayingSong = CurrentPlayingSongSP()
        if(songItem == null){
            SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(this.requireContext(), currentPlayingSong)
            return
        }
        currentPlayingSong.id = songItem.id
        currentPlayingSong.position = position.toLong()
        currentPlayingSong.fileName = songItem.fileName
        currentPlayingSong.fileName = songItem.fileName
        currentPlayingSong.uri = songItem.uri
        currentPlayingSong.currentSeekDuration = mCurrentDuration
        currentPlayingSong.artist = songItem.artist
        currentPlayingSong.duration = songItem.duration
        currentPlayingSong.title = songItem.title
        currentPlayingSong.typeMime = songItem.typeMime
        currentPlayingSong.uriTreeId = songItem.uriTreeId
        SharedPreferenceManagerUtils.Player.saveCurrentPlayingSong(this.requireContext(), currentPlayingSong)
        SharedPreferenceManagerUtils.Player.saveQueueListSize(this.requireContext(), mPlayerPagerAdapter?.itemCount ?: 0)
    }

    private fun initViews() {
        mSongItemViewModel = ModelsViewModelFactory(this.requireContext()).create(SongItemViewModel::class.java)

        mFragmentPlayerBinding.blurredImageview.layout(0,0,0,0)
        mFragmentPlayerBinding.textTitle.isSelected = true
        mFragmentPlayerBinding.textArtist.isSelected = true

        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentPlayerBinding.linearRescanDeviceContainer)
        ViewInsetModifiersUtils.updateTopViewInsets(mFragmentPlayerBinding.linearViewpager)
        ViewInsetModifiersUtils.updateBottomViewInsets(mFragmentPlayerBinding.linearControls)

        if(mPlayerMoreBottomSheetDialog == null)
            mPlayerMoreBottomSheetDialog = PlayerMoreBottomSheetDialog(mMainFragmentViewModel, mFragmentPlayerBinding.root)
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