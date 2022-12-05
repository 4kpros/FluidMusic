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
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel

class SortContentExplorerIBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var mBottomSheetSortContentExplorerBinding: BottomSheetSortContentExplorerBinding? = null

    private var mGenericListenDataViewModel: GenericListenDataViewModel? = null

    private var mFromSource: String? = null
    private var mFromSourceValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBottomSheetSortContentExplorerBinding = DataBindingUtil.inflate(inflater, R.layout._bottom_sheet_sort_content_explorer, container, false)
        val view = mBottomSheetSortContentExplorerBinding?.root

        initViews()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
    }

    private fun checkInteractions() {
        mBottomSheetSortContentExplorerBinding?.let { viewBiding ->
            viewBiding.radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
                onRadioGroupStateChanged(
                    checkedId
                )
            }
            viewBiding.checkboxInvertItems.setOnCheckedChangeListener { _, isChecked ->
                onInvertSortChanged(
                    isChecked
                )
            }
        }
    }


    private fun onInvertSortChanged(checked: Boolean) {
        mGenericListenDataViewModel?.setIsInverted(checked)
    }

    private fun onRadioGroupStateChanged(checkedId: Int) {
        mBottomSheetSortContentExplorerBinding?.let { viewBiding ->
            when (checkedId) {
                viewBiding.radioName.id -> {
                    mGenericListenDataViewModel?.setSortBy("name")
                }
                viewBiding.radioArtist.id -> {
                    mGenericListenDataViewModel?.setSortBy("artist")
                }
                viewBiding.radioAlbum.id -> {
                    mGenericListenDataViewModel?.setSortBy("album")
                }
                viewBiding.radioAlbumArtist.id -> {
                    mGenericListenDataViewModel?.setSortBy("albumArtist")
                }
                viewBiding.radioYear.id -> {
                    mGenericListenDataViewModel?.setSortBy("year")
                }
                viewBiding.radioLastAddedDateToLibrary.id -> {
                    mGenericListenDataViewModel?.setSortBy("lastAddedDateToLibrary")
                }
                viewBiding.radioLastUpdateDate.id -> {
                    mGenericListenDataViewModel?.setSortBy("lastUpdateDate")
                }
                viewBiding.radioTotalDuration.id -> {
                    mGenericListenDataViewModel?.setSortBy("totalDuration")
                }
                viewBiding.radioNumberTracks.id -> {
                    mGenericListenDataViewModel?.setSortBy("numberTracks")
                }
                viewBiding.radioNumberArtists.id -> {
                    mGenericListenDataViewModel?.setSortBy("numberArtists")
                }
                viewBiding.radioNumberAlbums.id -> {
                    mGenericListenDataViewModel?.setSortBy("numberAlbums")
                }
                viewBiding.radioNumberAlbumArtists.id -> {
                    mGenericListenDataViewModel?.setSortBy("numberAlbumArtists")
                }
                viewBiding.radioNumberComposers.id -> {
                    mGenericListenDataViewModel?.setSortBy("numberComposers")
                }
                else -> {
                    mGenericListenDataViewModel?.setSortBy("name")
                }
            }
        }
    }

    private fun initViews() {
        setupInitialStateUI()
        updateDefaultCheckedRadioButtonUI()
        updateDefaultCheckboxInvertButtonUI()
    }

    private fun setupInitialStateUI() {
        showAllRadioButtonsVisibility()
        updateRadioButtonsForSortGenericItems()
    }
    private fun updateRadioButtonsForSortGenericItems() {
        //Update generic first radio button text title
        mBottomSheetSortContentExplorerBinding?.let { viewBiding ->
            when (mFromSource) {
                ConstantValues.EXPLORE_ALBUMS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewBiding.radioName.tooltipText = context?.getString(R.string.album_name)
                    }
                    viewBiding.radioName.text = context?.getString(R.string.album_name)
                    //Now hide non necessary radio buttons
                    viewBiding.radioArtist.visibility = GONE
                    viewBiding.radioAlbum.visibility = GONE
                    viewBiding.radioAlbumArtist.visibility = GONE
                }
                ConstantValues.EXPLORE_ALBUM_ARTISTS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewBiding.radioName.tooltipText = context?.getString(R.string.album_artist)
                    }
                    viewBiding.radioName.text = context?.getString(R.string.album_artist)
                    //Now hide non necessary radio buttons
                    viewBiding.radioArtist.visibility = GONE
                    viewBiding.radioAlbum.visibility = GONE
                    viewBiding.radioAlbumArtist.visibility = GONE
                }
                ConstantValues.EXPLORE_ARTISTS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewBiding.radioName.tooltipText = context?.getString(R.string.artist_name)
                    }
                    viewBiding.radioName.text = context?.getString(R.string.artist_name)
                    //Now hide non necessary radio buttons
                    viewBiding.radioArtist.visibility = GONE
                }
                ConstantValues.EXPLORE_COMPOSERS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewBiding.radioName.tooltipText = context?.getString(R.string.composer_name)
                    }
                    viewBiding.radioName.text = context?.getString(R.string.composer_name)
                    //Now hide non necessary radio buttons
                    viewBiding.radioNumberComposers.visibility = GONE
                }
                ConstantValues.EXPLORE_FOLDERS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewBiding.radioName.tooltipText = context?.getString(R.string.folder_name)
                    }
                    viewBiding.radioName.text = context?.getString(R.string.folder_name)
                }
                ConstantValues.EXPLORE_GENRES -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewBiding.radioName.tooltipText = context?.getString(R.string.genre)
                    }
                    viewBiding.radioName.text = context?.getString(R.string.genre)
                }
                ConstantValues.EXPLORE_YEARS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewBiding.radioName.tooltipText = context?.getString(R.string.year)
                    }
                    viewBiding.radioName.text = context?.getString(R.string.year)
                    //Now hide non necessary radio buttons
                    viewBiding.radioYear.visibility = GONE
                }
            }
        }
    }
    private fun showAllRadioButtonsVisibility() {
        mBottomSheetSortContentExplorerBinding?.let { viewBiding ->
            viewBiding.radioName.visibility = VISIBLE
            viewBiding.radioArtist.visibility = VISIBLE
            viewBiding.radioAlbum.visibility = VISIBLE
            viewBiding.radioAlbumArtist.visibility = VISIBLE
            viewBiding.radioYear.visibility = VISIBLE
            viewBiding.radioLastAddedDateToLibrary.visibility = VISIBLE
            viewBiding.radioLastUpdateDate.visibility = VISIBLE
            viewBiding.radioTotalDuration.visibility = VISIBLE
            viewBiding.radioNumberTracks.visibility = VISIBLE
            viewBiding.radioNumberArtists.visibility = VISIBLE
            viewBiding.radioNumberAlbums.visibility = VISIBLE
            viewBiding.radioNumberAlbumArtists.visibility = VISIBLE
            viewBiding.radioNumberComposers.visibility = VISIBLE
        }
    }

    private fun updateDefaultCheckboxInvertButtonUI() {
        mBottomSheetSortContentExplorerBinding?.checkboxInvertItems?.isChecked = mGenericListenDataViewModel?.getIsInverted()?.value ?: false
    }
    private fun updateDefaultCheckedRadioButtonUI() {
        if(mGenericListenDataViewModel == null) return
        mBottomSheetSortContentExplorerBinding?.let { viewBiding ->
            val tempFilter = mGenericListenDataViewModel?.getSortBy()?.value ?: return
            when (tempFilter) {
                "name" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioName.id)
                }
                "artist" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioArtist.id)
                }
                "album" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioAlbum.id)
                }
                "albumArtist" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioAlbumArtist.id)
                }
                "year" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioYear.id)
                }
                "lastAddedDateToLibrary" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioLastAddedDateToLibrary.id)
                }
                "lastUpdateDate" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioLastUpdateDate.id)
                }
                "totalDuration" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioTotalDuration.id)
                }
                "numberTracks" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioNumberTracks.id)
                }
                "numberArtists" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioNumberArtists.id)
                }
                "numberAlbums" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioNumberAlbums.id)
                }
                "numberAlbumArtists" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioNumberAlbumArtists.id)
                }
                "numberComposers" -> {
                    viewBiding.radioGroupSort.check(viewBiding.radioNumberComposers.id)
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
        const val TAG = "SortContentExplorerIBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance() =
            SortContentExplorerIBottomSheetDialogFragment().apply {
            }
    }
}