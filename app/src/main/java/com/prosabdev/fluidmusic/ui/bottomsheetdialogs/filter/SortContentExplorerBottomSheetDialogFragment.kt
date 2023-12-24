package com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetSortContentExplorerBinding
import com.prosabdev.fluidmusic.databinding.FragmentYearsBinding
import com.prosabdev.fluidmusic.ui.fragments.PlaylistsFragment
import com.prosabdev.fluidmusic.ui.fragments.StreamsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.AlbumArtistsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.AlbumsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.ArtistsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.ComposersFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.FoldersFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.GenresFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.YearsFragment
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel

class SortContentExplorerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    //Data binding
    private lateinit var mDataBinding: BottomSheetSortContentExplorerBinding

    //View models
    private var mGenericListenDataViewModel: GenericListenDataViewModel? = null

    private var mFromSource: String? = null
    private var mFromSourceValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflate binding layout and return binding object
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout._bottom_sheet_sort_content_explorer, container, false)
        val view = mDataBinding.root

        //Load your UI content
        initViews()
        checkInteractions()

        return view
    }

    private fun checkInteractions() {
        mDataBinding.run {
            radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
                onRadioGroupStateChanged(
                    checkedId
                )
            }
            checkboxInvertItems.setOnCheckedChangeListener { _, isChecked ->
                onInvertSortChanged(
                    isChecked
                )
            }
        }
    }


    private fun onInvertSortChanged(checked: Boolean) {
        mGenericListenDataViewModel?.isInverted?.value = checked
    }

    private fun onRadioGroupStateChanged(checkedId: Int) {
        mDataBinding.run {
            when (checkedId) {
                radioName.id -> mGenericListenDataViewModel?.sortBy?.value = "name"
                radioArtist.id -> mGenericListenDataViewModel?.sortBy?.value = "artist"
                radioAlbum.id -> mGenericListenDataViewModel?.sortBy?.value = "album"
                radioAlbumArtist.id -> mGenericListenDataViewModel?.sortBy?.value = "albumArtist"
                radioYear.id -> mGenericListenDataViewModel?.sortBy?.value = "year"
                radioLastAddedDateToLibrary.id -> mGenericListenDataViewModel?.sortBy?.value = "lastAddedDateToLibrary"
                radioLastUpdateDate.id -> mGenericListenDataViewModel?.sortBy?.value = "lastUpdateDate"
                radioTotalDuration.id -> mGenericListenDataViewModel?.sortBy?.value = "totalDuration"
                radioNumberTracks.id -> mGenericListenDataViewModel?.sortBy?.value = "numberTracks"
                radioNumberArtists.id -> mGenericListenDataViewModel?.sortBy?.value = "numberArtists"
                radioNumberAlbums.id -> mGenericListenDataViewModel?.sortBy?.value = "numberAlbums"
                radioNumberAlbumArtists.id -> mGenericListenDataViewModel?.sortBy?.value = "numberAlbumArtists"
                radioNumberComposers.id -> mGenericListenDataViewModel?.sortBy?.value = "numberComposers"
                else -> mGenericListenDataViewModel?.sortBy?.value = "name"
            }
        }
    }

    private fun initViews() {
        updateTitleUI()
        setupInitialStateUI()
        updateDefaultCheckedRadioButtonUI()
        updateDefaultCheckboxInvertButtonUI()
    }
    private fun updateTitleUI() {
        when (mFromSource) {
            AlbumsFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.albums) ?: ""
                ) ?: ""
            }
            AlbumArtistsFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.album_artists) ?: ""
                ) ?: ""
            }
            ArtistsFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.artists) ?: ""
                ) ?: ""
            }
            ComposersFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.composers) ?: ""
                ) ?: ""
            }
            FoldersFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.folders) ?: ""
                ) ?: ""
            }
            GenresFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.genres) ?: ""
                ) ?: ""
            }
            YearsFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.years) ?: ""
                ) ?: ""
            }
            PlaylistsFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.playlists) ?: ""
                ) ?: ""
            }
//            FavoritesFragment.TAG -> {
//                mDataBinding.textSortDetails.text = context?.resources?.getString(
//                    R.string._sort_for,
//                    context?.resources?.getString(R.string.favorites) ?: ""
//                ) ?: ""
//            }
            StreamsFragment.TAG -> {
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.streams) ?: ""
                ) ?: ""
            }
        }
    }

    private fun setupInitialStateUI() {
        showAllRadioButtonsVisibility()
        updateRadioButtonsForSortGenericItems()
    }
    private fun updateRadioButtonsForSortGenericItems() {
        //Update generic first radio button text title
        mDataBinding.run {
            when (mFromSource) {
                AllSongsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        radioName.tooltipText = context?.getString(R.string.album_name)
                    }
                    radioName.text = context?.getString(R.string.album_name)
                    radioArtist.visibility = GONE
                    radioAlbum.visibility = GONE
                    radioAlbumArtist.visibility = GONE
                }
                AlbumArtistsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        radioName.tooltipText = context?.getString(R.string.album_artist)
                    }
                    radioName.text = context?.getString(R.string.album_artist)
                    radioArtist.visibility = GONE
                    radioAlbum.visibility = GONE
                    radioAlbumArtist.visibility = GONE
                }
                ArtistsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        radioName.tooltipText = context?.getString(R.string.artist_name)
                    }
                    radioName.text = context?.getString(R.string.artist_name)
                    radioArtist.visibility = GONE
                }
                ComposersFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        radioName.tooltipText = context?.getString(R.string.composer_name)
                    }
                    radioName.text = context?.getString(R.string.composer_name)
                    radioNumberComposers.visibility = GONE
                }
                FoldersFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        radioName.tooltipText = context?.getString(R.string.folder_name)
                    }
                    radioName.text = context?.getString(R.string.folder_name)
                }
                GenresFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        radioName.tooltipText = context?.getString(R.string.genre)
                    }
                    radioName.text = context?.getString(R.string.genre)
                }
                YearsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        radioName.tooltipText = context?.getString(R.string.year)
                    }
                    radioName.text = context?.getString(R.string.year)
                    radioYear.visibility = GONE
                }
            }
        }
    }
    private fun showAllRadioButtonsVisibility() {
        mDataBinding.run {
            radioName.visibility = VISIBLE
            radioArtist.visibility = VISIBLE
            radioAlbum.visibility = VISIBLE
            radioAlbumArtist.visibility = VISIBLE
            radioYear.visibility = VISIBLE
            radioLastAddedDateToLibrary.visibility = VISIBLE
            radioLastUpdateDate.visibility = VISIBLE
            radioTotalDuration.visibility = VISIBLE
            radioNumberTracks.visibility = VISIBLE
            radioNumberArtists.visibility = VISIBLE
            radioNumberAlbums.visibility = VISIBLE
            radioNumberAlbumArtists.visibility = VISIBLE
            radioNumberComposers.visibility = VISIBLE
        }
    }

    private fun updateDefaultCheckboxInvertButtonUI() {
        mDataBinding.checkboxInvertItems.isChecked = mGenericListenDataViewModel?.isInverted?.value ?: false
    }
    private fun updateDefaultCheckedRadioButtonUI() {
        if(mGenericListenDataViewModel == null) return
        mDataBinding.run {
            val tempFilter = mGenericListenDataViewModel?.sortBy?.value ?: return
            when (tempFilter) {
                "name" -> radioGroupSort.check(radioName.id)
                "artist" -> radioGroupSort.check(radioArtist.id)
                "album" -> radioGroupSort.check(radioAlbum.id)
                "albumArtist" -> radioGroupSort.check(radioAlbumArtist.id)
                "year" -> radioGroupSort.check(radioYear.id)
                "lastAddedDateToLibrary" -> radioGroupSort.check(radioLastAddedDateToLibrary.id)
                "lastUpdateDate" -> radioGroupSort.check(radioLastUpdateDate.id)
                "totalDuration" -> radioGroupSort.check(radioTotalDuration.id)
                "numberTracks" -> radioGroupSort.check(radioNumberTracks.id)
                "numberArtists" -> radioGroupSort.check(radioNumberArtists.id)
                "numberAlbums" -> radioGroupSort.check(radioNumberAlbums.id)
                "numberAlbumArtists" -> radioGroupSort.check(radioNumberAlbumArtists.id)
                "numberComposers" -> radioGroupSort.check(radioNumberComposers.id)
            }
        }
    }

    companion object {
        const val TAG = "SortContentExplorerBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance(genericListenDataViewModel: GenericListenDataViewModel?, fromSource: String?, fromSourceValue: String? = null) =
            SortContentExplorerBottomSheetDialogFragment().apply {
                mGenericListenDataViewModel = genericListenDataViewModel
                mFromSource = fromSource
                mFromSourceValue = fromSourceValue
            }
        @JvmStatic
        fun newInstance() =
            SortContentExplorerBottomSheetDialogFragment().apply {
            }
    }
}