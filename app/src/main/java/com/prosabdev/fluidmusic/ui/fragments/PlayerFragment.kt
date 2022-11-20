package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
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
import com.prosabdev.fluidmusic.ui.activities.settings.MediaScannerSettingsActivity
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.QueueMusicDialog
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.views.explore.SongItemViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.views.fragments.PlayerFragmentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs


@BuildCompat.PrereleaseSdkCheck class PlayerFragment : Fragment() {

    private lateinit var mFragmentPlayerBinding: FragmentPlayerBinding

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private lateinit var mPlayerFragmentViewModel: PlayerFragmentViewModel
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

    private fun loadPlayingLastSession() {
        //
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
    private fun updateCurrentPlayingSong(currentSong: Int?) {
        mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(currentSong ?: 0, true)
    }
    private fun updateDataFromSource(sourceOf: String?) {
        if(sourceOf == ConstantValues.EXPLORE_ALL_SONGS){
            mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner
            ) { currentSong -> updateCurrentPlayingSong(null) }
        }
    }
    private suspend fun updatePlayerUI(position: Int) = lifecycleScope.launch(context = Dispatchers.Default) {
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
        val tempBinary : ByteArray? = if(tempCurrentItem != null ) tempCurrentItem.covertArt?.binaryData else null
        CustomUILoaders.loadCovertArtFromBinaryData(mContext, mFragmentPlayerBinding.blurredImageview, tempBinary, 500)
    }

    private suspend fun checkInteractions() = lifecycleScope.launch(context = Dispatchers.Default){
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