package com.prosabdev.fluidmusic.ui.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
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
import com.prosabdev.fluidmusic.dialogs.PlayerMoreDialog
import com.prosabdev.fluidmusic.dialogs.PlayerQueueMusicDialog
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import kotlin.math.*


class PlayerFragment : Fragment() {
    private lateinit var mContext: Context
    private var mActivity: FragmentActivity? = null

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null
    private var mSongList: ArrayList<SongItem> = ArrayList<SongItem>()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    //Text view var
    private var mTextTitle: AppCompatTextView? = null
    private var mTextArtist: AppCompatTextView? = null
    private var mTextDurationCurrent: AppCompatTextView? = null
    private var mTextDuration: AppCompatTextView? = null
    //Slider view var
    private var mSlider: Slider? = null
    //Image view var
    private var mCovertArtBlurred: ImageView? = null
    //Buttons var
    private var mButtonPlayPause: MaterialButton? = null
    private var mButtonSkipNext: MaterialButton? = null
    private var mButtonSkipPrev: MaterialButton? = null
    private var mButtonShuffle: MaterialButton? = null
    private var mButtonRepeat: MaterialButton? = null
    //
    private var mButtonMore: MaterialButton? = null
    private var mButtonQueueMusic: MaterialButton? = null
    private var mButtonEqualizer: MaterialButton? = null
    private var mButtonArrowDown: MaterialButton? = null

    //Dialog var
    private var mPlayerMoreDialog: PlayerMoreDialog? = null
    private var mPlayerQueueMusicDialog: PlayerQueueMusicDialog? = null

    private var mLinearControls: LinearLayoutCompat? = null
    private var mPlayerViewPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_player, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupViewPagerAdapter()
        observeLiveData()
        checkInteractions()
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
        mPlayerViewPager?.adapter = mPlayerPagerAdapter
        mPlayerViewPager?.setCurrentItem(0, true)
        mPlayerViewPager?.clipToPadding = false
        mPlayerViewPager?.clipChildren = false
        //        mPlayerViewPager?.setOffscreenPageLimit(3);
        mPlayerViewPager?.getChildAt(0)?.overScrollMode = View.OVER_SCROLL_NEVER
        mPlayerViewPager?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                CustomMathComputations.fadeWithPageOffset(mCovertArtBlurred, positionOffset)
            }
        })
        transformPageScale(mPlayerViewPager)
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

        //Request load songs from database or media file scanner
        if(mPlayerFragmentViewModel.getIsLoadingInBackground().value == false && (mPlayerFragmentViewModel.getDataRequestCounter().value ?: 0) <= 0){
            mPlayerFragmentViewModel.requestLoadDataAsync(mActivity as Activity)
        }
        mPlayerFragmentViewModel.getSongList().observe(mActivity as LifecycleOwner, object : Observer<ArrayList<SongItem>>{
            override fun onChanged(songList: ArrayList<SongItem>?) {
                updateQueueList(songList)
            }
        })
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(currentSong: Int?) {
                updateCurrentPlayingSong(currentSong)
            }
        })
        mPlayerFragmentViewModel.getSourceOfQueueList().observe(mActivity as LifecycleOwner, object : Observer<String> {
            override fun onChanged(sourceOf: String?) {
                updateDataFromSource(sourceOf)
            }
        })
        mPlayerFragmentViewModel.getIsPlaying().observe(mActivity as LifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isPlaying: Boolean?) {
                updatePlayerButtonsUI()
            }
        })
        mPlayerFragmentViewModel.getShuffle().observe(mActivity as LifecycleOwner, object : Observer<Int> {
            override fun onChanged(shuffle: Int?) {
                updatePlayerButtonsUI()
            }
        })
        mPlayerFragmentViewModel.getRepeat().observe(mActivity as LifecycleOwner, object : Observer<Int> {
            override fun onChanged(repeat: Int?) {
                updatePlayerButtonsUI()
            }
        })
        mPlayerFragmentViewModel.getPlayingProgressValue().observe(mActivity as LifecycleOwner, object : Observer<Long> {
            override fun onChanged(progressValue: Long?) {
                updatePlayerButtonsUI()
            }
        })
    }

    private fun updatePlayerButtonsUI() {
        if(mPlayerFragmentViewModel.getIsPlaying().value == true){
            mButtonPlayPause?.icon = ContextCompat.getDrawable(mContext, R.drawable.pause_circle)
        }else{
            mButtonPlayPause?.icon = ContextCompat.getDrawable(mContext, R.drawable.play_circle)
        }
    }

    private fun updateCurrentPlayingSong(currentSong: Int?) {
        mPlayerViewPager?.currentItem = currentSong ?: 0
    }

    private fun updateDataFromSource(sourceOf: String?) {
        if(sourceOf != mPlayerFragmentViewModel.getSourceOfQueueList().value){
            updateQueueList(mPlayerFragmentViewModel.getSongList().value)
        }
    }

    private fun updateQueueList(songList: ArrayList<SongItem>?) {
        mSongList.clear()
        mSongList.addAll(songList!!)
        mPlayerPagerAdapter?.notifyItemRangeInserted(0, mSongList.size)
        mPlayerViewPager?.currentItem = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
    } private fun changeCurrentSong(position: Int) {
        mPlayerFragmentViewModel.setCurrentSong(position)
    }

    private fun updatePlayerUI(position: Int) {
        //Update current song info
        if(mSongList.size > 0 && position >= 0){
            if(mTextTitle != null)
                mTextTitle?.text = if(mSongList[position].title != null && mSongList[position].title!!.isNotEmpty()) mSongList[position].title else mSongList[position].fileName //Set song title

            if(mTextArtist != null)
                mTextArtist?.text = if(mSongList[position].artist!!.isNotEmpty()) mSongList[position].artist else mContext.getString(R.string.unknown_artist)

            if(mTextDurationCurrent != null)
                mTextDurationCurrent?.text = CustomFormatters.formatSongDurationToString(0)

            if(mTextDuration != null)
                mTextDuration?.text = CustomFormatters.formatSongDurationToString(mSongList[position].duration)

            if(mSlider != null)
                mSlider?.value = 0.0f
        }
        //Update blurred background
        val tempBinary : ByteArray? = if(mSongList.size > 0) mSongList[position].covertArt?.binaryData else null
        CustomUILoaders.loadBlurredWithImageLoader(mContext, mCovertArtBlurred, tempBinary, 100)
    }

    private fun checkInteractions() {
        mPlayerViewPager?.registerOnPageChangeCallback(object : OnPageChangeCallback(){
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
        mButtonPlayPause?.setOnClickListener(View.OnClickListener {
            onPlayPause()
        })
        mButtonSkipNext?.setOnClickListener(View.OnClickListener {
            onNextPage()
        })
        mButtonSkipPrev?.setOnClickListener(View.OnClickListener {
            onPrevPage()
        })
        mButtonShuffle?.setOnClickListener(View.OnClickListener {
            onShuffle()
        })
        mButtonRepeat?.setOnClickListener(View.OnClickListener {
            onRepeat()
        })
        //
        mButtonMore?.setOnClickListener(View.OnClickListener {
            mPlayerMoreDialog = PlayerMoreDialog()
            mPlayerMoreDialog?.show(childFragmentManager, PlayerMoreDialog.TAG)
        })
        mButtonQueueMusic?.setOnClickListener(View.OnClickListener {
            mPlayerQueueMusicDialog = PlayerQueueMusicDialog()
            mPlayerQueueMusicDialog?.show(childFragmentManager, PlayerQueueMusicDialog.TAG)
        })
        mButtonEqualizer?.setOnClickListener(View.OnClickListener {
            mActivity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                add<EqualizerFragment>(R.id.main_activity_fragment_container)
                addToBackStack(null)
            }
        })
        mButtonArrowDown?.setOnClickListener(View.OnClickListener {
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

    private fun initViews(view: View) {
        mTextTitle = view.findViewById(R.id.text_title)
        mTextArtist = view.findViewById(R.id.text_artist)
        mTextDurationCurrent = view.findViewById(R.id.text_duration_current)
        mTextDuration = view.findViewById(R.id.text_duration)
        mSlider = view.findViewById(R.id.slider)

        mCovertArtBlurred = view.findViewById(R.id.blurred_imageview)

        mButtonPlayPause = view.findViewById(R.id.button_play_pause)
        mButtonSkipNext = view.findViewById(R.id.button_skip_next)
        mButtonSkipPrev = view.findViewById(R.id.button_skip_prev)
        mButtonShuffle = view.findViewById(R.id.button_shuffle)
        mButtonRepeat = view.findViewById(R.id.button_repeat)
        //
        mButtonQueueMusic = view.findViewById(R.id.button_queue_music)
        mButtonEqualizer = view.findViewById(R.id.button_equalizer)
        mButtonArrowDown = view.findViewById(R.id.button_arrow_down)

        mPlayerViewPager = view.findViewById<ViewPager2>(R.id.view_pager_player)
        mLinearControls = view.findViewById<LinearLayoutCompat>(R.id.linear_controls)

        CustomViewModifiers.updateTopViewInsets(view.findViewById(R.id.linear_viewpager))
        CustomViewModifiers.updateBottomViewInsets(mLinearControls!!)
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