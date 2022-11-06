package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.button.MaterialButton
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.PlayerPageAdapter
import com.prosabdev.fluidmusic.dialogs.PlayerMoreDialog
import com.prosabdev.fluidmusic.dialogs.QueueMusicDialog
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomFormatters
import com.prosabdev.fluidmusic.utils.CustomUILoaders
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.viewmodels.PlayerFragmentViewModel
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jaudiotagger.tag.images.Artwork
import kotlin.math.abs


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
    //Image view var
    private var mCovertArtBlurred: ImageView? = null
    //Buttons var
    private var mButtonMore: MaterialButton? = null
    private var mButtonQueueMusic: MaterialButton? = null
    private var mButtonEqualizer: MaterialButton? = null
    private var mButtonArrowDown: MaterialButton? = null
    //Dialog var
    private var mPlayerMoreDialog: PlayerMoreDialog? = null
    private var mQueueMusicDialog: QueueMusicDialog? = null

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

        MainScope().launch {
            initViews(view)
            setupViewPagerAdapter()
            observeLiveData()
            blurViews(view)
            checkInteractions()
        }
    }

    private fun blurViews(view: View) {
//        val radius = 25f
//
//        val decorView : View = view.rootView
//        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
//        val rootView : ViewGroup = decorView.findViewById(android.R.id.content)
//
//        // Optional:
//        // Set drawable to draw in the beginning of each blurred frame.
//        // Can be used in case your layout has a lot of transparent space and your content
//        // gets a too low alpha value after blur is applied.
//        val windowBackground : Drawable = decorView.background
//        mBlurView?.setupWith(rootView, RenderScriptBlur(mContext)) // or RenderEffectBlur
//            ?.setFrameClearDrawable(windowBackground) // Optional
//            ?.setBlurRadius(radius)

    }

    private fun setupViewPagerAdapter() {
        mPlayerPagerAdapter =
            PlayerPageAdapter(mSongList, mContext, object : PlayerPageAdapter.OnItemClickListener {
                override fun onViewPagerClicked(position: Int) {
                    Toast.makeText(context, "onViewPagerClicked", Toast.LENGTH_SHORT).show()
                }

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
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
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
        mPlayerFragmentViewModel.getQueueList().observe(mActivity as LifecycleOwner, object : Observer<ArrayList<SongItem>>{
            override fun onChanged(songList: ArrayList<SongItem>?) {
                Log.i(ConstantValues.TAG, "On queue list data changed ${songList?.size}")
                MainScope().launch {
                    updateQueueList(songList)
                }
            }
        })
        mPlayerFragmentViewModel.getCurrentSong().observe(mActivity as LifecycleOwner, object : Observer<Int>{
            override fun onChanged(currentSong: Int?) {
                Log.i(ConstantValues.TAG, "On queue list current song changed $currentSong")
                MainScope().launch {
                    updateCurrentPlayingSong(currentSong)
                }
            }
        })
    }

    private fun updateCurrentPlayingSong(currentSong: Int?) {
        mPlayerViewPager?.currentItem = currentSong ?: 0
    }

    private fun updateQueueList(songList: ArrayList<SongItem>?) {
        mSongList.clear()
        mSongList.addAll(songList!!)
        mPlayerPagerAdapter?.notifyDataSetChanged()
        if((mPlayerViewPager?.currentItem ?: 0) != mPlayerFragmentViewModel.getCurrentSong().value){
            mPlayerViewPager?.currentItem = mPlayerFragmentViewModel.getCurrentSong().value ?: 0
        }
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
                updatePlayerUI(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        mButtonMore?.setOnClickListener(View.OnClickListener {
            mPlayerMoreDialog = PlayerMoreDialog()
            mPlayerMoreDialog?.show(childFragmentManager, PlayerMoreDialog.TAG)
        })
        mButtonQueueMusic?.setOnClickListener(View.OnClickListener {
            mQueueMusicDialog = QueueMusicDialog()
            mQueueMusicDialog?.show(childFragmentManager, QueueMusicDialog.TAG)
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

    private fun updatePlayerUI(position: Int) {
        //Update current song info

        mTextTitle?.text = if(mSongList[position].title != null && mSongList[position].title!!.isNotEmpty()) mSongList[position].title else mSongList[position].fileName //Set song title
        mTextArtist?.text = if(mSongList[position].artist!!.isNotEmpty()) mSongList[position].artist else mContext.getString(R.string.unknown_artist)
        mTextDurationCurrent?.text = CustomFormatters.formatSongDurationToString(mSongList[position].duration)
        mTextDuration?.text = CustomFormatters.formatSongDurationToString(mSongList[position].duration)

        //Update blurred background
        val tempBinaryData : ByteArray? = mSongList[position].covertArt?.binaryData
        CustomUILoaders.loadBlurredWithImageLoader(mContext, mCovertArtBlurred, tempBinaryData, 100)
    }

    private fun initViews(view: View) {
        mTextTitle = view.findViewById(R.id.text_title)
        mTextArtist = view.findViewById(R.id.text_artist)
        mTextDurationCurrent = view.findViewById(R.id.text_duration_current)
        mTextDuration = view.findViewById(R.id.text_duration)

        mCovertArtBlurred = view.findViewById(R.id.blurred_imageview)

        mButtonQueueMusic = view.findViewById(R.id.button_queue_music)
        mButtonEqualizer = view.findViewById(R.id.button_equalizer)
        mButtonArrowDown = view.findViewById(R.id.button_arrow_down)

        mPlayerViewPager = view.findViewById<ViewPager2>(R.id.view_pager_player)
        mLinearControls = view.findViewById<LinearLayoutCompat>(R.id.linear_controls)

        CustomViewModifiers.updateTopViewInsets(view.findViewById(R.id.linear_viewpager))
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