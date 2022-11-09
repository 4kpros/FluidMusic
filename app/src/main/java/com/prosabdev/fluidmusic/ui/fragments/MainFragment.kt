package com.prosabdev.fluidmusic.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.*
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var mContext: Context
    private var mActivity: FragmentActivity? = null

    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private var mTextTitleMiniPlayer: AppCompatTextView? = null
    private var mTextArtistMiniPlayer: AppCompatTextView? = null
    private var mProgressIndicatorMiniPlayer: LinearProgressIndicator? = null
    private var mCovertArtMiniPlayer: ImageView? = null
    private var mBlurredCovertArtMiniPlayer: ImageView? = null
    private var mButtonPlayPause: MaterialButton? = null
    private var mButtonSkipNext: MaterialButton? = null

    private var mSlidingUpPanel: SlidingUpPanelLayout? = null

    private var mMainFragmentContainer: FrameLayout? = null
    private var mPlayerFragmentContainer: FrameLayout? = null
    private var mMiniPlayerContainer: ConstraintLayout? = null
    private var mPlayerViewsContainer: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<MainExploreFragment>(R.id.main_fragment_container)
        }
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<PlayerFragment>(R.id.player_fragment_container)
        }

        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        initViews(view)
        checkInteractions()

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
    }

    override fun onResume() {
        updateMiniPlayerUI(false)
        super.onResume()
    }

    private fun observeLiveData() {
        mPlayerFragmentViewModel.getSongList().observe(mActivity as LifecycleOwner, object :
            Observer<ArrayList<SongItem>> {
            override fun onChanged(songList: ArrayList<SongItem>?) {
                updateMiniPlayerUI()
            }
        })
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner, object :
            Observer<Int> {
            override fun onChanged(currentSong: Int?) {
                updateMiniPlayerUI()
            }
        })
        mPlayerFragmentViewModel.getSourceOfQueueList().observe(mActivity as LifecycleOwner, object :
            Observer<String> {
            override fun onChanged(sourceOf: String?) {
                updateMiniPlayerUI()
            }
        })
        mPlayerFragmentViewModel.getIsPlaying().observe(mActivity as LifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isPlaying: Boolean?) {
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
            mButtonPlayPause?.icon = ContextCompat.getDrawable(mContext, R.drawable.pause)
        }else{
            mButtonPlayPause?.icon = ContextCompat.getDrawable(mContext, R.drawable.play_arrow)
        }
    }

    var mUpdateMiniPlayerUIJob : Job? = null
    private fun updateMiniPlayerUI(animate : Boolean = true) {
        //Update current song info
        val tempQL : ArrayList<SongItem>? = mPlayerFragmentViewModel.getSongList().value
        val tempPositionInQL : Int = mPlayerFragmentViewModel.getCurrentSong().value ?: -1
        if(tempQL!= null && tempQL.size > 0 && tempPositionInQL >= 0){
            var tempTitle : String = ""
            var tempArtist : String = ""
            if(mTextTitleMiniPlayer != null)
                tempTitle = if(tempQL[tempPositionInQL].title != null && tempQL[tempPositionInQL].title!!.isNotEmpty()) tempQL[tempPositionInQL].title.toString() else tempQL[tempPositionInQL].fileName.toString() //Set song title

            if(mTextArtistMiniPlayer != null)
                tempArtist = if(tempQL[tempPositionInQL].artist != null && tempQL[tempPositionInQL].artist!!.isNotEmpty()) tempQL[tempPositionInQL].artist.toString() else mContext.getString(R.string.unknown_artist)

            val tempBinary : ByteArray? = if(tempQL.size > 0) tempQL[tempPositionInQL].covertArt?.binaryData else null

            if(animate){
                CustomUILoaders.loadCovertArtFromBinaryData(mContext, mCovertArtMiniPlayer, tempBinary, 100)
                if(mUpdateMiniPlayerUIJob != null)
                    mUpdateMiniPlayerUIJob?.cancel()
                mUpdateMiniPlayerUIJob = MainScope().launch {
                    animateCrossFadeOutInTextView(mTextTitleMiniPlayer, tempTitle, 100)
                    animateCrossFadeOutInTextView(mTextArtistMiniPlayer, tempArtist, 100)
                    animateCrossFadeOutInImage(mBlurredCovertArtMiniPlayer, tempBinary, true, 10, 100)
                }
            }else{
                CustomUILoaders.loadCovertArtFromBinaryData(mContext, mCovertArtMiniPlayer, tempBinary, 100)
                CustomUILoaders.loadBlurredWithImageLoader(mContext, mBlurredCovertArtMiniPlayer, tempBinary)
            }
        }
    }
    private suspend fun animateCrossFadeOutInTextView(
        textView: AppCompatTextView?,
        textValue : String,
        durationInterval : Int = 150
    ) {
        textView?.apply {
            View.VISIBLE
            animate()
                .alpha(0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(durationInterval.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        textView.text = textValue
                        CustomAnimators.crossFadeUp(textView as View, true, durationInterval)
                    }
                })
        }
    }

    private suspend fun animateCrossFadeOutInImage(
        imageView: ImageView?,
        tempBinary: ByteArray?,
        blurred : Boolean = false,
        width : Int = 100,
        durationInterval : Int = 150
    ) {
        imageView?.apply {
            View.VISIBLE
            animate()
                .alpha(0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(durationInterval.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if(!blurred){
                            CustomUILoaders.loadCovertArtFromBinaryData(mContext, imageView, tempBinary, width)
                        }else{
                            CustomUILoaders.loadBlurredWithImageLoader(mContext, imageView, tempBinary, width)
                        }
                        CustomAnimators.crossFadeUp(imageView, true, durationInterval)
                    }
                })
        }
    }

    private fun checkInteractions() {
        mButtonSkipNext?.setOnClickListener(View.OnClickListener {
            onNextPage()
        })
        mButtonPlayPause?.setOnClickListener(View.OnClickListener {
            onPlayPause()
        })
        mSlidingUpPanel?.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                Log.i(ConstantValues.TAG, "Slide panel offset : $slideOffset")
                //Mini player visibility
//                if(1.0f - (slideOffset * 5.0f) >= 0.0f){
//                    mMiniPlayerContainer?.alpha = 1.0f - (slideOffset * 5.0f)
//                }else{
//                    mMiniPlayerContainer?.alpha = 0.0f
//                }
//                if(slideOffset < 1.0f){
//                    mMiniPlayerContainer?.visibility = VISIBLE
//                }else{
//                    mMiniPlayerContainer?.visibility = GONE
//                }

                //Player visibility
                if(slideOffset <= 0.21f){
//                    mPlayerFragmentContainer?.alpha = 0.0f
                    mSlidingUpPanel?.setDragView(mMiniPlayerContainer)
                }else {
//                    mPlayerFragmentContainer?.alpha = (slideOffset * 1.21f) - 0.21f
                    mSlidingUpPanel?.setDragView(mPlayerFragmentContainer)
                }
//                if (slideOffset <= 0.15f){
//                    mPlayerFragmentContainer?.visibility = GONE
//                }else{
//                    mPlayerFragmentContainer?.visibility = VISIBLE
//                }
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: PanelState?,
                newState: PanelState
            ) {
                Log.i(ConstantValues.TAG, "onPanelStateChanged $newState")
            }
        })
    }

    fun onPlayPause(){
        val tempPP : Boolean = !(mPlayerFragmentViewModel.getIsPlaying().value ?: false)
        mPlayerFragmentViewModel.setIsPlaying(tempPP)
    }
    fun onNextPage(){
        val tempCS :Int = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
        val tempSongListSize :Int = mPlayerFragmentViewModel.getSongList().value?.size ?: 0
        if(tempSongListSize > 0 && tempCS < tempSongListSize - 1)
            mPlayerFragmentViewModel.setCurrentSong(tempCS + 1)
    }

    private fun initViews(view: View) {

        mSlidingUpPanel = view.findViewById(R.id.sliding_up_panel)
        mMainFragmentContainer = view.findViewById(R.id.main_fragment_container)
        mPlayerFragmentContainer = view.findViewById(R.id.player_fragment_container)
        mMiniPlayerContainer = view.findViewById(R.id.constraint_mini_player)

        mTextTitleMiniPlayer = view.findViewById(R.id.text_mini_player_title)
        mTextArtistMiniPlayer = view.findViewById(R.id.text_mini_player_artist)
        mProgressIndicatorMiniPlayer = view.findViewById(R.id.progress_mini_player_indicator)
        mCovertArtMiniPlayer = view.findViewById(R.id.imageview_mini_player)
        mBlurredCovertArtMiniPlayer = view.findViewById(R.id.imageview_blurred_mini_player)

        mButtonPlayPause = view.findViewById(R.id.button_play_pause)
        mButtonSkipNext = view.findViewById(R.id.button_skip_next)

        mCovertArtMiniPlayer?.layout(0,0,0,0)
        mBlurredCovertArtMiniPlayer?.layout(0,0,0,0)
        mTextTitleMiniPlayer?.isSelected = true
        mTextArtistMiniPlayer?.isSelected = true

        CustomViewModifiers.updateTopViewInsets(mMainFragmentContainer!!)
//        CustomViewModifiers.removeBottomViewInsets(mSlidingUpPanel!!)
        CustomViewModifiers.updateBottomViewInsets(mMiniPlayerContainer as View)

//        CustomViewModifiers.updateBottomViewInsets(mSlidingUpPanel!!)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}