package com.prosabdev.fluidmusic.ui.fragments.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.HeadlinePlayShuffleAdapter
import com.prosabdev.fluidmusic.databinding.FragmentArtistsBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.ArtistsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.models.explore.ArtistItemViewModel

class ArtistsFragment : Fragment() {

    private lateinit var mFragmentArtistsBinding: FragmentArtistsBinding

    private val mArtistsFragmentViewModel: ArtistsFragmentViewModel by activityViewModels()
    private val mMainFragmentViewModel: MainFragmentViewModel by activityViewModels()
    private val mPlayerFragmentViewModel: PlayerFragmentViewModel by activityViewModels()

    private val mArtistItemViewModel: ArtistItemViewModel by viewModels()

    private var mHeadlineTopPlayShuffleAdapter: HeadlinePlayShuffleAdapter? = null
    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mIsDraggingToScroll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentArtistsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_artists, container,false)
        val view = mFragmentArtistsBinding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val TAG = "ArtistsFragment"
        private const val ORGANIZE_LIST_GRID_DEFAULT_VALUE: Int = ConstantValues.ORGANIZE_GRID_MEDIUM

        @JvmStatic
        fun newInstance() =
            ArtistsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}