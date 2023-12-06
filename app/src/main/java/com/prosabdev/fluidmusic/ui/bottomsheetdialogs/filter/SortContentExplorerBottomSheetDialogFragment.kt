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

    private lateinit var mDataBiding: BottomSheetSortContentExplorerBinding

    private var mGenericListenDataViewModel: GenericListenDataViewModel? = null

    private var mFromSource: String? = null
    private var mFromSourceValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
        mDataBiding = DataBindingUtil.inflate(inflater, R.layout._bottom_sheet_sort_content_explorer, container, false)
        val view = mDataBiding.root

        //Load your UI content
        initViews()
        checkInteractions()

        return view
    }

    private fun checkInteractions() {
        mDataBiding.let {
            it.radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
                onRadioGroupStateChanged(
                    checkedId
                )
            }
            it.checkboxInvertItems.setOnCheckedChangeListener { _, isChecked ->
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
        mDataBiding.let {
            when (checkedId) {
                it.radioName.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "name"
                }
                it.radioArtist.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "artist"
                }
                it.radioAlbum.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "album"
                }
                it.radioAlbumArtist.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "albumArtist"
                }
                it.radioYear.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "year"
                }
                it.radioLastAddedDateToLibrary.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "lastAddedDateToLibrary"
                }
                it.radioLastUpdateDate.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "lastUpdateDate"
                }
                it.radioTotalDuration.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "totalDuration"
                }
                it.radioNumberTracks.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "numberTracks"
                }
                it.radioNumberArtists.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "numberArtists"
                }
                it.radioNumberAlbums.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "numberAlbums"
                }
                it.radioNumberAlbumArtists.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "numberAlbumArtists"
                }
                it.radioNumberComposers.id -> {
                    mGenericListenDataViewModel?.sortBy?.value = "numberComposers"
                }
                else -> {
                    mGenericListenDataViewModel?.sortBy?.value = "name"
                }
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
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.albums) ?: ""
                ) ?: ""
            }
            AlbumArtistsFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.album_artists) ?: ""
                ) ?: ""
            }
            ArtistsFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.artists) ?: ""
                ) ?: ""
            }
            ComposersFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.composers) ?: ""
                ) ?: ""
            }
            FoldersFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.folders) ?: ""
                ) ?: ""
            }
            GenresFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.genres) ?: ""
                ) ?: ""
            }
            YearsFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.years) ?: ""
                ) ?: ""
            }
            PlaylistsFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.playlists) ?: ""
                ) ?: ""
            }
//            FavoritesFragment.TAG -> {
//                mDataBiding.textSortDetails.text = context?.resources?.getString(
//                    R.string._sort_for,
//                    context?.resources?.getString(R.string.favorites) ?: ""
//                ) ?: ""
//            }
            StreamsFragment.TAG -> {
                mDataBiding.textSortDetails.text = context?.resources?.getString(
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
        mDataBiding.let {
            when (mFromSource) {
                AllSongsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.radioName.tooltipText = context?.getString(R.string.album_name)
                    }
                    it.radioName.text = context?.getString(R.string.album_name)
                    //Now hide non necessary radio buttons
                    it.radioArtist.visibility = GONE
                    it.radioAlbum.visibility = GONE
                    it.radioAlbumArtist.visibility = GONE
                }
                AlbumArtistsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.radioName.tooltipText = context?.getString(R.string.album_artist)
                    }
                    it.radioName.text = context?.getString(R.string.album_artist)
                    //Now hide non necessary radio buttons
                    it.radioArtist.visibility = GONE
                    it.radioAlbum.visibility = GONE
                    it.radioAlbumArtist.visibility = GONE
                }
                ArtistsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.radioName.tooltipText = context?.getString(R.string.artist_name)
                    }
                    it.radioName.text = context?.getString(R.string.artist_name)
                    //Now hide non necessary radio buttons
                    it.radioArtist.visibility = GONE
                }
                ComposersFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.radioName.tooltipText = context?.getString(R.string.composer_name)
                    }
                    it.radioName.text = context?.getString(R.string.composer_name)
                    //Now hide non necessary radio buttons
                    it.radioNumberComposers.visibility = GONE
                }
                FoldersFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.radioName.tooltipText = context?.getString(R.string.folder_name)
                    }
                    it.radioName.text = context?.getString(R.string.folder_name)
                }
                GenresFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.radioName.tooltipText = context?.getString(R.string.genre)
                    }
                    it.radioName.text = context?.getString(R.string.genre)
                }
                YearsFragment.TAG -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.radioName.tooltipText = context?.getString(R.string.year)
                    }
                    it.radioName.text = context?.getString(R.string.year)
                    //Now hide non necessary radio buttons
                    it.radioYear.visibility = GONE
                }
            }
        }
    }
    private fun showAllRadioButtonsVisibility() {
        mDataBiding.let {
            it.radioName.visibility = VISIBLE
            it.radioArtist.visibility = VISIBLE
            it.radioAlbum.visibility = VISIBLE
            it.radioAlbumArtist.visibility = VISIBLE
            it.radioYear.visibility = VISIBLE
            it.radioLastAddedDateToLibrary.visibility = VISIBLE
            it.radioLastUpdateDate.visibility = VISIBLE
            it.radioTotalDuration.visibility = VISIBLE
            it.radioNumberTracks.visibility = VISIBLE
            it.radioNumberArtists.visibility = VISIBLE
            it.radioNumberAlbums.visibility = VISIBLE
            it.radioNumberAlbumArtists.visibility = VISIBLE
            it.radioNumberComposers.visibility = VISIBLE
        }
    }

    private fun updateDefaultCheckboxInvertButtonUI() {
        mDataBiding.checkboxInvertItems.isChecked = mGenericListenDataViewModel?.isInverted?.value ?: false
    }
    private fun updateDefaultCheckedRadioButtonUI() {
        if(mGenericListenDataViewModel == null) return
        mDataBiding.let {
            val tempFilter = mGenericListenDataViewModel?.sortBy?.value ?: return
            when (tempFilter) {
                "name" -> {
                    it.radioGroupSort.check(it.radioName.id)
                }
                "artist" -> {
                    it.radioGroupSort.check(it.radioArtist.id)
                }
                "album" -> {
                    it.radioGroupSort.check(it.radioAlbum.id)
                }
                "albumArtist" -> {
                    it.radioGroupSort.check(it.radioAlbumArtist.id)
                }
                "year" -> {
                    it.radioGroupSort.check(it.radioYear.id)
                }
                "lastAddedDateToLibrary" -> {
                    it.radioGroupSort.check(it.radioLastAddedDateToLibrary.id)
                }
                "lastUpdateDate" -> {
                    it.radioGroupSort.check(it.radioLastUpdateDate.id)
                }
                "totalDuration" -> {
                    it.radioGroupSort.check(it.radioTotalDuration.id)
                }
                "numberTracks" -> {
                    it.radioGroupSort.check(it.radioNumberTracks.id)
                }
                "numberArtists" -> {
                    it.radioGroupSort.check(it.radioNumberArtists.id)
                }
                "numberAlbums" -> {
                    it.radioGroupSort.check(it.radioNumberAlbums.id)
                }
                "numberAlbumArtists" -> {
                    it.radioGroupSort.check(it.radioNumberAlbumArtists.id)
                }
                "numberComposers" -> {
                    it.radioGroupSort.check(it.radioNumberComposers.id)
                }
            }
        }
    }

    fun updateBottomSheetData(genericListenDataViewModel: GenericListenDataViewModel?, fromSource: String?, fromSourceValue: String? = null){
        mGenericListenDataViewModel = genericListenDataViewModel
        mFromSource = fromSource
        mFromSourceValue = fromSourceValue
        initViews()
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