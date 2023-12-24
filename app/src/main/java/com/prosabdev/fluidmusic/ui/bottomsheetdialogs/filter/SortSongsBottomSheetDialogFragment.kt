package com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetSortSongsBinding
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentForFragment
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

class SortSongsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    //Data binding
    private lateinit var mDataBinding: BottomSheetSortSongsBinding

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
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout._bottom_sheet_sort_songs, container, false)
        val view = mDataBinding.root

        //Load your UI content
        initViews()
        checkInteractions()

        return view
    }

    private fun checkInteractions() {
        mDataBinding.let {
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
        when (checkedId) {
            R.id.radio_title -> {
                mGenericListenDataViewModel?.sortBy?.value = "title"
            }
            R.id.radio_file_name -> {
                mGenericListenDataViewModel?.sortBy?.value = "fileName"
            }
            R.id.radio_track_number -> {
                mGenericListenDataViewModel?.sortBy?.value = "cdTrackNumber"
            }
            R.id.radio_disc_number -> {
                mGenericListenDataViewModel?.sortBy?.value = "diskNumber"
            }
            R.id.radio_artist -> {
                mGenericListenDataViewModel?.sortBy?.value = "artist"
            }
            R.id.radio_album -> {
                mGenericListenDataViewModel?.sortBy?.value = "album"
            }
            R.id.radio_album_artist -> {
                mGenericListenDataViewModel?.sortBy?.value = "albumArtist"
            }
            R.id.radio_composer -> {
                mGenericListenDataViewModel?.sortBy?.value = "composer"
            }
            R.id.radio_year -> {
                mGenericListenDataViewModel?.sortBy?.value = "year"
            }
            R.id.radio_duration -> {
                mGenericListenDataViewModel?.sortBy?.value = "duration"
            }
            R.id.radio_last_added_date_to_library -> {
                mGenericListenDataViewModel?.sortBy?.value = "lastAddedDateToLibrary"
            }
            R.id.radio_last_update_date -> {
                mGenericListenDataViewModel?.sortBy?.value = "lastUpdateDate"
            }
            R.id.radio_path -> {
                mGenericListenDataViewModel?.sortBy?.value = "path"
            }
            R.id.radio_path_case_sensitive -> {
                mGenericListenDataViewModel?.sortBy?.value = "pathCaseSensitive"
            }
            R.id.radio_genre -> {
                mGenericListenDataViewModel?.sortBy?.value = "genre"
            }
            R.id.radio_size -> {
                mGenericListenDataViewModel?.sortBy?.value = "size"
            }
            R.id.radio_type_mime -> {
                mGenericListenDataViewModel?.sortBy?.value = "typeMime"
            }
            R.id.radio_rating -> {
                mGenericListenDataViewModel?.sortBy?.value = "rating"
            }
            R.id.radio_play_count -> {
                mGenericListenDataViewModel?.sortBy?.value = "playCount"
            }
            R.id.radio_last_played -> {
                mGenericListenDataViewModel?.sortBy?.value = "lastPlayer"
            }
            R.id.radio_author -> {
                mGenericListenDataViewModel?.sortBy?.value = "author"
            }
            R.id.radio_writer -> {
                mGenericListenDataViewModel?.sortBy?.value = "writer"
            }
            R.id.radio_language -> {
                mGenericListenDataViewModel?.sortBy?.value = "language"
            }
            R.id.radio_sample_rate -> {
                mGenericListenDataViewModel?.sortBy?.value = "sampleRate"
            }
            R.id.radio_bitrate -> {
                mGenericListenDataViewModel?.sortBy?.value = "bitrate"
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
                mDataBinding.textSortDetails.text = context?.resources?.getString(
                    R.string._sort_for,
                    context?.resources?.getString(R.string.all_songs) ?: ""
                ) ?: ""
            }
            ExploreContentForFragment.TAG -> {
                when (mFromSourceValue) {
                    AlbumsFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_album) ?: ""
                        ) ?: ""
                    }
                    AlbumArtistsFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_album_artist) ?: ""
                        ) ?: ""
                    }
                    ArtistsFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_artist) ?: ""
                        ) ?: ""
                    }
                    ComposersFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_composer) ?: ""
                        ) ?: ""
                    }
                    FoldersFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_folder) ?: ""
                        ) ?: ""
                    }
                    GenresFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_genre) ?: ""
                        ) ?: ""
                    }
                    YearsFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_year) ?: ""
                        ) ?: ""
                    }
                    PlaylistsFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_playlist) ?: ""
                        ) ?: ""
                    }
//                    FavoritesFragment.TAG -> {
//                        mDataBinding.textSortDetails.text = context?.resources?.getString(
//                            R.string._sort_for,
//                            context?.resources?.getString(R.string.songs_for_favorites) ?: ""
//                        ) ?: ""
//                    }
                    StreamsFragment.TAG -> {
                        mDataBinding.textSortDetails.text = context?.resources?.getString(
                            R.string._sort_for,
                            context?.resources?.getString(R.string.songs_for_stream) ?: ""
                        ) ?: ""
                    }
                }
            }
        }
    }

    private fun updateDefaultCheckboxInvertButtonUI() {
        mDataBinding.checkboxInvertItems.isChecked =
            mGenericListenDataViewModel?.isInverted?.value ?: false
    }

    private fun updateDefaultCheckedRadioButtonUI() {
        if (mGenericListenDataViewModel == null) return
        mDataBinding.let {
            val tempFilter = mGenericListenDataViewModel?.sortBy?.value ?: return
            when (tempFilter) {
                "title" -> {
                    it.radioGroupSort.check(R.id.radio_title)
                }
                "fileName" -> {
                    it.radioGroupSort.check(R.id.radio_file_name)
                }
                "cdTrackNumber" -> {
                    it.radioGroupSort.check(R.id.radio_artist)
                }
                "diskNumber" -> {
                    it.radioGroupSort.check(R.id.radio_disc_number)
                }
                "artist" -> {
                    it.radioGroupSort.check(R.id.radio_artist)
                }
                "album" -> {
                    it.radioGroupSort.check(R.id.radio_album)
                }
                "albumArtist" -> {
                    it.radioGroupSort.check(R.id.radio_album_artist)
                }
                "composer" -> {
                    it.radioGroupSort.check(R.id.radio_composer)
                }
                "year" -> {
                    it.radioGroupSort.check(R.id.radio_year)
                }
                "duration" -> {
                    it.radioGroupSort.check(R.id.radio_duration)
                }
                "lastAddedDateToLibrary" -> {
                    it.radioGroupSort.check(R.id.radio_last_added_date_to_library)
                }
                "lastUpdateDate" -> {
                    it.radioGroupSort.check(R.id.radio_last_update_date)
                }
                "path" -> {
                    it.radioGroupSort.check(R.id.radio_path)
                }
                "pathCaseSensitive" -> {
                    it.radioGroupSort.check(R.id.radio_path_case_sensitive)
                }
                "genre" -> {
                    it.radioGroupSort.check(R.id.radio_genre)
                }
                "size" -> {
                    it.radioGroupSort.check(R.id.radio_size)
                }
                "typeMime" -> {
                    it.radioGroupSort.check(R.id.radio_type_mime)
                }
                "rating" -> {
                    it.radioGroupSort.check(R.id.radio_rating)
                }
                "playCount" -> {
                    it.radioGroupSort.check(R.id.radio_play_count)
                }
                "lastPlayed" -> {
                    it.radioGroupSort.check(R.id.radio_last_played)
                }
                "author" -> {
                    it.radioGroupSort.check(R.id.radio_author)
                }
                "writer" -> {
                    it.radioGroupSort.check(R.id.radio_writer)
                }
                "language" -> {
                    it.radioGroupSort.check(R.id.radio_language)
                }
                "sampleRate" -> {
                    it.radioGroupSort.check(R.id.radio_sample_rate)
                }
                "bitrate" -> {
                    it.radioGroupSort.check(R.id.radio_bitrate)
                }
                else -> {
                    it.radioGroupSort.check(it.radioTitle.id)
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