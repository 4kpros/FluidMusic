package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.*
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
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import jp.wasabeef.blurry.Blurry
import org.jaudiotagger.tag.images.Artwork
import kotlin.math.abs


class PlayerFragment : Fragment() {
    private lateinit var mContext: Context
    private var mActivity: FragmentActivity? = null

    private var mPlayerPagerAdapter: PlayerPageAdapter? = null
    private var mSongList: MutableList<SongItem>? = null
    private val mCurrentPosition = 0
    private val mPlayerPageFragmentViewModel: MainExploreFragmentViewModel by viewModels()

    //Buttons var
    private var mButtonMore: MaterialButton? = null
    private var mButtonQueueMusic: MaterialButton? = null
    private var mButtonEqualizer: MaterialButton? = null
    private var mButtonArrowDown: MaterialButton? = null
    //Dialog var
    private var mPlayerMoreDialog: PlayerMoreDialog? = null
    private var mQueueMusicDialog: QueueMusicDialog? = null

    private var mPlayerContainer: LinearLayoutCompat? = null
    private var mPlayerViewPager: ViewPager2? = null
    private var mCovertArtBlurred: ImageView? = null
    private var mBlurView: BlurView? = null

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
        blurViews(view)
        setupViewPagerAdapter()
        checkInteractions(view)
        observeLiveData(view)
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
        mSongList = mutableListOf<SongItem>()
        mSongList?.add(SongItem("Drole de parcours", "La fouine"))
        mSongList?.add(SongItem("Drole de parcours", "La fouine"))
        mSongList?.add(SongItem("Drole de parcours", "La fouine"))
        mSongList?.add(SongItem("Drole de parcours", "La fouine"))
        mSongList?.add(SongItem("Drole de parcours", "La fouine"))
        mSongList?.add(SongItem("Drole de parcours", "La fouine"))
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

    private fun observeLiveData(view: View) {
        //
    }

    private fun checkInteractions(view: View) {
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

        val drawable = ContextCompat.getDrawable(mContext.applicationContext, R.drawable.ic_fluid_music_icon_with_padding)
        if (drawable != null) {
            setBlurredFromImage(drawable)
        }
    }

    private fun setCovertArtFromPosition(position: Int) {
        //Set covert art from song position
        if((mSongList != null && mSongList!!.isNotEmpty())){
            val tempArtwork: Artwork? = mSongList!![position].covertArt
            var tempBinaryData: ByteArray? = null
            if (tempArtwork != null) tempBinaryData = tempArtwork.binaryData

            if(tempBinaryData != null && tempBinaryData.isNotEmpty()){
                setBlurredFromImage(tempBinaryData)
                return
            }
        }
        //Set covert art from drawable
        val drawable = ContextCompat.getDrawable(mContext.applicationContext, R.drawable.fashion)
        if (drawable != null) {
            setBlurredFromImage(drawable)
        }
    }

    private fun setBlurredFromImage(binaryData: ByteArray) {
        val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap?>?
            ) {
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                //
            }
        }
        if (binaryData.isNotEmpty()){
            Glide.with(mContext)
                .asBitmap()
                .load(binaryData)
                .useAnimationPool(true)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .dontAnimate()
                .centerCrop()
                .apply(RequestOptions().override(380, 200))
                .into(customTarget)
        }else{
            val drawable = ContextCompat.getDrawable(mContext.applicationContext, R.drawable.fashion)
            if (drawable != null) {
                setBlurredFromImage(drawable)
            }
        }
    }

    private fun setBlurredFromImage(drawableValue: Drawable) {
        val customTarget: CustomTarget<Bitmap?> = object : CustomTarget<Bitmap?>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap?>?
            ) {
                Blurry.with(context).from(resource).into(mCovertArtBlurred)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                //
            }
        }
        Glide.with(mContext)
            .load(ContextCompat.getDrawable(mContext.applicationContext, R.drawable.fashion))
            .useAnimationPool(true)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .dontAnimate()
            .centerCrop()
            .apply(RequestOptions().override(380, 200))
            .into(mCovertArtBlurred!!)

    }


    private fun initViews(view: View) {
        mButtonMore = view.findViewById(R.id.button_more)
        mButtonQueueMusic = view.findViewById(R.id.button_queue_music)
        mButtonEqualizer = view.findViewById(R.id.button_equalizer)
        mButtonArrowDown = view.findViewById(R.id.button_arrow_down)

        mPlayerViewPager = view.findViewById<ViewPager2>(R.id.view_pager_player)
        mCovertArtBlurred = view.findViewById<ImageView>(R.id.blur_imageview)
        mPlayerContainer = view.findViewById<LinearLayoutCompat>(R.id.player_container)

        updateTopViewInsets(mPlayerViewPager)
        updateBottomViewInsets(view.findViewById(R.id.relative_player))
        updateBottomViewInsets(view.findViewById(R.id.linear_mini_player))
    }

    private fun updateTopViewInsets(view: View?) {
        view?.setOnApplyWindowInsetsListener { view, insets ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                val tempInsets = insets.getInsets(WindowInsetsCompat.Type.systemGestures())
                view.updatePadding(
                    tempInsets.top,
                )
            }else{
                view.updatePadding(
                    top = insets.systemWindowInsetTop,
                )
            }
            WindowInsetsCompat.CONSUMED
            insets
        }
    }
    private fun updateBottomViewInsets(view: View?) {
        view?.setOnApplyWindowInsetsListener { view, insets ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                val tempInsets = insets.getInsets(WindowInsetsCompat.Type.systemGestures())
                view.updatePadding(
                    tempInsets.bottom,
                )
            }else{
                view.updatePadding(
                    bottom = insets.systemWindowInsetBottom,
                )
            }
            WindowInsetsCompat.CONSUMED
            insets
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}