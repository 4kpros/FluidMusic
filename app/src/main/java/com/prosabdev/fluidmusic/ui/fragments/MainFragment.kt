package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
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

    private var mSlidingUpPanel: SlidingUpPanelLayout? = null

    private var mMainFragmentContainer: FrameLayout? = null
    private var mPlayerFragmentContainer: FrameLayout? = null
    private var mMiniPlayerContainer: ConstraintLayout? = null
    private var mPlayerViewsContainer: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        mContext = requireContext()
        mActivity = requireActivity()

        mActivity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            add<MainExploreFragment>(R.id.main_fragment_container)
        }
        mActivity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            add<PlayerFragment>(R.id.player_fragment_container)
        }
        initViews(view)
        observeLiveData()
        checkInteractions()

        // Inflate the layout for this fragment
        return view
    }

    override fun onResume() {
        updateMiniPlayerUI()
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
    }
    private fun updateMiniPlayerUI() {
        //Update current song info
        val tempQL : ArrayList<SongItem>? = mPlayerFragmentViewModel.getSongList().value
        val tempPositionInQL : Int = mPlayerFragmentViewModel.getCurrentSong().value ?: -1
        if(tempQL!= null && tempQL.size > 0 && tempPositionInQL >= 0){
            if(mTextTitleMiniPlayer != null)
                mTextTitleMiniPlayer?.text = if(tempQL[tempPositionInQL].title != null && tempQL[tempPositionInQL].title!!.isNotEmpty()) tempQL[tempPositionInQL].title else tempQL[tempPositionInQL].fileName //Set song title

            if(mTextArtistMiniPlayer != null)
                mTextArtistMiniPlayer?.text = if(tempQL[tempPositionInQL].artist != null && tempQL[tempPositionInQL].artist!!.isNotEmpty()) tempQL[tempPositionInQL].artist else mContext.getString(R.string.unknown_artist)

            val tempBinary : ByteArray? = if(tempQL.size > 0) tempQL[tempPositionInQL].covertArt?.binaryData else null
            CustomUILoaders.loadCovertArtFromBinaryData(mContext, mCovertArtMiniPlayer, tempBinary, 100)
            CustomUILoaders.loadBlurredWithImageLoader(mContext, mBlurredCovertArtMiniPlayer, tempBinary)
        }
    }

    private fun checkInteractions() {
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
        mButtonPlayPause = view.findViewById(R.id.button_mini_player_play_pause)

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