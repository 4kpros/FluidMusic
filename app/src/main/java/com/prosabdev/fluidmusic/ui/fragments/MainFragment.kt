package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*

class MainFragment : Fragment() {

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private var mSlidingUpPanel: SlidingUpPanelLayout? = null

    private var mMainFragmentContainer: FrameLayout? = null
    private var mPlayerFragmentContainer: FrameLayout? = null
    private var mMiniPlayerContainer: LinearLayoutCompat? = null

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

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        checkInteractions(view)
    }

    private fun checkInteractions(view: View) {

        mMiniPlayerContainer?.alpha = 1.0f
        mPlayerFragmentContainer?.alpha = 0.0f
        mSlidingUpPanel?.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                Log.i(ConstantValues.TAG, "onPanelSlide, offset $slideOffset")
                mMiniPlayerContainer?.alpha = if (1.0f - (slideOffset * 5.0f) >= 0.0f) (1.0f - (slideOffset * 5.0f)) else 0.0f
                mPlayerFragmentContainer?.alpha = if (slideOffset <= 0.21f) 0.0f else slideOffset
                if (slideOffset <= 0.15f){
                    mPlayerFragmentContainer?.visibility = GONE
                }else{
                    mPlayerFragmentContainer?.visibility = VISIBLE
                }
                if(slideOffset < 1.0f){
                    mMiniPlayerContainer?.visibility = VISIBLE
                }else{
                    mMiniPlayerContainer?.visibility = GONE
                }
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
        mActivity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            add<MainExploreFragment>(R.id.main_fragment_container)
        }
        mActivity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            add<PlayerFragment>(R.id.player_fragment_container)
        }
        mMainFragmentContainer = view.findViewById(R.id.main_fragment_container)
        mPlayerFragmentContainer = view.findViewById(R.id.player_fragment_container)
        mSlidingUpPanel = view.findViewById(R.id.sliding_up_panel)
        mMiniPlayerContainer = view.findViewById(R.id.linear_mini_player)
        updateTopViewInsets(mMainFragmentContainer)
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
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}