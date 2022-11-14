package com.prosabdev.fluidmusic.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.databinding.FragmentMainBinding
import com.prosabdev.fluidmusic.databinding.FragmentPlayerBinding
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerQueueMusicDialog
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*


class PlayerFragment : Fragment() {

    private lateinit var mFragmentPlayerBinding: FragmentPlayerBinding

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null
    private var mSongList: ArrayList<SongItem> = ArrayList<SongItem>()

    //Dialog var
    private var mPlayerMoreDialog: PlayerMoreDialog? = null
    private var mPlayerQueueMusicDialog: PlayerQueueMusicDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        setupViewPagerAdapter()
        checkInteractions()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    override fun onResume() {
        updatePlayerUI(mPlayerFragmentViewModel.getCurrentSong().value ?: 0)
        super.onResume()
    }

    private fun setupViewPagerAdapter() {
        mPlayerPagerAdapter =
            PlayerPageAdapter(mSongList, mContext, object : PlayerPageAdapter.OnItemClickListener {
                override fun onButtonLyricsClicked(position: Int) {
                    Toast.makeText(context, "onButtonLyricsClicked", Toast.LENGTH_SHORT).show()
                }

                override fun onButtonFullscreenClicked(position: Int) {
                    Toast.makeText(context, "onButtonFullscreenClicked", Toast.LENGTH_SHORT).show()
                }

            })
        mFragmentPlayerBinding.viewPagerPlayer.adapter = mPlayerPagerAdapter
        mFragmentPlayerBinding.viewPagerPlayer.setCurrentItem(0, true)
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
        mPlayerFragmentViewModel.getSourceOfQueueList().observe(mActivity as LifecycleOwner
        ) { sourceOf -> updateDataFromSource(sourceOf) }
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner
        ) { t -> updateCurrentPlayingSong(t) }
        mPlayerFragmentViewModel.getIsPlaying().observe(mActivity as LifecycleOwner
        ) { updatePlayerButtonsUI() }
        mPlayerFragmentViewModel.getShuffle().observe(mActivity as LifecycleOwner
        ) { shuffle -> updateShuffleUI(shuffle) }
        mPlayerFragmentViewModel.getRepeat().observe(mActivity as LifecycleOwner
        ) { repeat -> updateRepeatUI(repeat) }
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
            mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner, object : Observer<Int>{
                override fun onChanged(currentSong: Int?) {
                    updateCurrentPlayingSong(currentSong)
                }
            })
        }
    }

    private suspend fun updateQueueList(songList: ArrayList<SongItem>?) = coroutineScope{
        if (songList != null) {
            mSongList.clear()
            val startPosition: Int = mSongList.size
            val itemCount: Int = songList.size
            mSongList.addAll(startPosition, songList)
            mPlayerPagerAdapter?.notifyItemRangeInserted(startPosition, itemCount)
        }
        mFragmentPlayerBinding.viewPagerPlayer.currentItem = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
    }
    private fun changeCurrentSong(position: Int) {
        mPlayerFragmentViewModel.setCurrentSong(position)
    }

    private fun updatePlayerUI(position: Int) {
        //Update current song info
        if(mSongList.size > 0 && position >= 0){
            mFragmentPlayerBinding.textTitle.text = if(mSongList[position].title != null && mSongList[position].title!!.isNotEmpty()) mSongList[position].title else mSongList[position].fileName //Set song title

            mFragmentPlayerBinding.textArtist.text = if(mSongList[position].artist!!.isNotEmpty()) mSongList[position].artist else mContext.getString(R.string.unknown_artist)

            mFragmentPlayerBinding.textDurationCurrent.text = CustomFormatters.formatSongDurationToString(0)

            mFragmentPlayerBinding.textDuration.text = CustomFormatters.formatSongDurationToString(mSongList[position].duration)

            mFragmentPlayerBinding.slider.value = 0.0f
        }
        //Update blurred background
        val tempBinary : ByteArray? = if(mSongList.size > 0) mSongList[position].covertArt?.binaryData else null
        CustomUILoaders.loadBlurredWithImageLoader(mContext, mFragmentPlayerBinding.blurredImageview, tempBinary, 100)
    }

    private fun checkInteractions() {
        mFragmentPlayerBinding.viewPagerPlayer.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                //
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                changeCurrentSong(position)
                updatePlayerUI(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        mFragmentPlayerBinding.buttonPlayPause.setOnClickListener(View.OnClickListener {
            onPlayPause()
        })
        mFragmentPlayerBinding.buttonSkipNext.setOnClickListener(View.OnClickListener {
            onNextPage()
        })
        mFragmentPlayerBinding.buttonSkipPrev.setOnClickListener(View.OnClickListener {
            onPrevPage()
        })
        mFragmentPlayerBinding.buttonShuffle.setOnClickListener(View.OnClickListener {
            onShuffle()
        })
        mFragmentPlayerBinding.buttonRepeat.setOnClickListener(View.OnClickListener {
            onRepeat()
        })
        //
        mFragmentPlayerBinding.buttonMore.setOnClickListener(View.OnClickListener {
            if(mPlayerMoreDialog == null)
                mPlayerMoreDialog = PlayerMoreDialog()
            mPlayerMoreDialog?.show(childFragmentManager, PlayerMoreDialog.TAG)
        })
        mFragmentPlayerBinding.buttonQueueMusic.setOnClickListener(View.OnClickListener {
            mPlayerQueueMusicDialog = PlayerQueueMusicDialog()
            mPlayerQueueMusicDialog?.show(childFragmentManager, PlayerQueueMusicDialog.TAG)
        })
        mFragmentPlayerBinding.buttonEqualizer.setOnClickListener(View.OnClickListener {
            mActivity.supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<EqualizerFragment>(R.id.main_activity_fragment_container)
                addToBackStack(null)
            }
        })
        mFragmentPlayerBinding.buttonArrowDown.setOnClickListener(View.OnClickListener {
            //
        })
    }

    fun onPlayPause(){
        val tempPP : Boolean = !(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        mPlayerFragmentViewModel.setIsPlaying(tempPP)
    }
    fun onNextPage(){
        val tempCS :Int = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
        if(mSongList.size > 0 && tempCS < mSongList.size-1)
            mPlayerFragmentViewModel.setCurrentSong(tempCS + 1)
    }
    fun onPrevPage(){
        val tempCS :Int = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
        if(tempCS > 0)
            mPlayerFragmentViewModel.setCurrentSong(tempCS - 1)
    }
    fun onShuffle(){
        val tempS :Int = mPlayerFragmentViewModel.getShuffle().value ?: 0
//        if(tempS > 0)
//            mPlayerFragmentViewModel.setShuffle(tempS)
    }
    fun onRepeat(){
        val tempR :Int = mPlayerFragmentViewModel.getRepeat().value ?: 0
//        if(tempR > 0)
//            mPlayerFragmentViewModel.setRepeat(tempR)
    }

    private fun initViews() {
        mFragmentPlayerBinding.blurredImageview.layout(0,0,0,0)
        mFragmentPlayerBinding.textTitle.isSelected = true
        mFragmentPlayerBinding.textArtist.isSelected = true

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