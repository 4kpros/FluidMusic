package com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetSortSongsBinding
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.ui.fragments.PlaylistsFragment
import com.prosabdev.fluidmusic.ui.fragments.StreamsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel

class SortSongsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var mDataBidingView: BottomSheetSortSongsBinding? = null

    private var mGenericListenDataViewModel: GenericListenDataViewModel? = null

    private var mFromSource: String? = null
    private var mFromSourceValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBidingView =
            DataBindingUtil.inflate(inflater, R.layout._bottom_sheet_sort_songs, container, false)
        val view = mDataBidingView?.root

        initViews()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInteractions()
    }

    private fun checkInteractions() {
        mDataBidingView?.let { bottomSheetSortSongsBinding ->
            bottomSheetSortSongsBinding.radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
                onRadioGroupStateChanged(
                    checkedId
                )
            }
            bottomSheetSortSongsBinding.checkboxInvertItems.setOnCheckedChangeListener { _, isChecked ->
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
        when (checkedId) {
            R.id.radio_title -> {
                mGenericListenDataViewModel?.setSortBy("title")
            }
            R.id.radio_file_name -> {
                mGenericListenDataViewModel?.setSortBy("fileName")
            }
            R.id.radio_track_number -> {
                mGenericListenDataViewModel?.setSortBy("cdTrackNumber")
            }
            R.id.radio_disc_number -> {
                mGenericListenDataViewModel?.setSortBy("diskNumber")
            }
            R.id.radio_artist -> {
                mGenericListenDataViewModel?.setSortBy("artist")
            }
            R.id.radio_album -> {
                mGenericListenDataViewModel?.setSortBy("album")
            }
            R.id.radio_album_artist -> {
                mGenericListenDataViewModel?.setSortBy("albumArtist")
            }
            R.id.radio_composer -> {
                mGenericListenDataViewModel?.setSortBy("composer")
            }
            R.id.radio_year -> {
                mGenericListenDataViewModel?.setSortBy("year")
            }
            R.id.radio_duration -> {
                mGenericListenDataViewModel?.setSortBy("duration")
            }
            R.id.radio_last_added_date_to_library -> {
                mGenericListenDataViewModel?.setSortBy("lastAddedDateToLibrary")
            }
            R.id.radio_last_update_date -> {
                mGenericListenDataViewModel?.setSortBy("lastUpdateDate")
            }
            R.id.radio_path -> {
                mGenericListenDataViewModel?.setSortBy("path")
            }
            R.id.radio_path_case_sensitive -> {
                mGenericListenDataViewModel?.setSortBy("pathCaseSensitive")
            }
            R.id.radio_genre -> {
                mGenericListenDataViewModel?.setSortBy("genre")
            }
            R.id.radio_size -> {
                mGenericListenDataViewModel?.setSortBy("size")
            }
            R.id.radio_type_mime -> {
                mGenericListenDataViewModel?.setSortBy("typeMime")
            }
            R.id.radio_rating -> {
                mGenericListenDataViewModel?.setSortBy("rating")
            }
            R.id.radio_play_count -> {
                mGenericListenDataViewModel?.setSortBy("playCount")
            }
            R.id.radio_last_played -> {
                mGenericListenDataViewModel?.setSortBy("lastPlayer")
            }
            R.id.radio_author -> {
                mGenericListenDataViewModel?.setSortBy("author")
            }
            R.id.radio_writer -> {
                mGenericListenDataViewModel?.setSortBy("writer")
            }
            R.id.radio_language -> {
                mGenericListenDataViewModel?.setSortBy("language")
            }
            R.id.radio_sample_rate -> {
                mGenericListenDataViewModel?.setSortBy("sampleRate")
            }
            R.id.radio_bitrate -> {
                mGenericListenDataViewModel?.setSortBy("bitrate")
            }
        }
    }

    private fun initViews() {
        updateTitleUI()
        updateDefaultCheckboxInvertButtonUI()
        updateDefaultCheckedRadioButtonUI()
    }
    private fun updateTitleUI() {
        when (mFromSource) {
            AllSongsFragment.TAG -> {
                mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.all_songs) ?: ""
            }
            ExploreContentsForFragment.TAG -> {
                when (mFromSourceValue) {
                    AlbumsFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_album) ?: ""
                    }
                    AlbumArtistsFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_album_artist) ?: ""
                    }
                    ArtistsFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_artist) ?: ""
                    }
                    ComposersFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_composer) ?: ""
                    }
                    FoldersFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_folder) ?: ""
                    }
                    GenresFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_genre) ?: ""
                    }
                    YearsFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_year) ?: ""
                    }
                    PlaylistsFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_playlist) ?: ""
                    }
//                    FavoritesFragment.TAG -> {
//                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_favorites) ?: ""
//                    }
                    StreamsFragment.TAG -> {
                        mDataBidingView?.textSortDetails?.text = context?.resources?.getString(R.string.songs_for_stream) ?: ""
                    }
                }
            }
        }
    }

    private fun updateDefaultCheckboxInvertButtonUI() {
        mDataBidingView?.checkboxInvertItems?.isChecked =
            mGenericListenDataViewModel?.getIsInverted()?.value ?: false
    }

    private fun updateDefaultCheckedRadioButtonUI() {
        if (mGenericListenDataViewModel == null) return
        mDataBidingView?.let { bottomSheetSortSongsBinding ->
            val tempFilter = mGenericListenDataViewModel?.getSortBy()?.value ?: return
            when (tempFilter) {
                "title" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_title)
                }
                "fileName" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_file_name)
                }
                "cdTrackNumber" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_artist)
                }
                "diskNumber" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_disc_number)
                }
                "artist" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_artist)
                }
                "album" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_album)
                }
                "albumArtist" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_album_artist)
                }
                "composer" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_composer)
                }
                "year" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_year)
                }
                "duration" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_duration)
                }
                "lastAddedDateToLibrary" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_last_added_date_to_library)
                }
                "lastUpdateDate" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_last_update_date)
                }
                "path" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_path)
                }
                "pathCaseSensitive" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_path_case_sensitive)
                }
                "genre" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_genre)
                }
                "size" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_size)
                }
                "typeMime" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_type_mime)
                }
                "rating" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_rating)
                }
                "playCount" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_play_count)
                }
                "lastPlayed" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_last_played)
                }
                "author" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_author)
                }
                "writer" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_writer)
                }
                "language" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_language)
                }
                "sampleRate" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_sample_rate)
                }
                "bitrate" -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(R.id.radio_bitrate)
                }
                else -> {
                    bottomSheetSortSongsBinding.radioGroupSort.check(bottomSheetSortSongsBinding.radioTitle.id)
                }
            }
        }
    }

    fun updateBottomSheetData(
        genericListenDataViewModel: GenericListenDataViewModel?,
        fromSource: String?,
        fromSourceValue: String? = null
    ) {
        mGenericListenDataViewModel = genericListenDataViewModel
        mFromSource = fromSource
        mFromSourceValue = fromSourceValue
        initViews()
    }

    companion object {
        const val TAG = "SortItemsBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance(
            genericListenDataViewModel: GenericListenDataViewModel?,
            fromSource: String?,
            fromSourceValue: String? = null
        ) =
            SortSongsBottomSheetDialogFragment().apply {
                mGenericListenDataViewModel = genericListenDataViewModel
                mFromSource = fromSource
                mFromSourceValue = fromSourceValue
            }

        @JvmStatic
        fun newInstance() =
            SortSongsBottomSheetDialogFragment().apply {
            }
    }
}