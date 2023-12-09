package com.prosabdev.fluidmusic.ui.bottomsheetdialogs.filter

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prosabdev.common.constants.MainConst
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.BottomSheetOrganizeItemsBinding
import com.prosabdev.fluidmusic.databinding.FragmentYearsBinding
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

class OrganizeItemBottomSheetDialogFragment : BottomSheetDialogFragment(), OnShowListener {

    //Data binding
    private lateinit var mDataBinding: BottomSheetOrganizeItemsBinding

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
        mDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout._bottom_sheet_organize_items,
            container,
            false
        )
        val view = mDataBinding.root

        //Load your UI content
        initViews()
        checkInteractions()

        return view
    }

    private fun checkInteractions() {
        mDataBinding.radioGroupOrganize.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_list_extra_small -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_LIST_EXTRA_SMALL

                R.id.radio_list_small -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_LIST_SMALL

                R.id.radio_list_medium -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_LIST_MEDIUM

                R.id.radio_list_large -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_LIST_LARGE

                R.id.radio_list_small_no_image -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_LIST_SMALL_NO_IMAGE

                R.id.radio_list_medium_no_image -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_LIST_MEDIUM_NO_IMAGE

                R.id.radio_list_large_no_image -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_LIST_LARGE_NO_IMAGE

                R.id.radio_grid_extra_small -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_GRID_EXTRA_SMALL

                R.id.radio_grid_small -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_GRID_SMALL

                R.id.radio_grid_medium -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_GRID_MEDIUM

                R.id.radio_grid_large -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_GRID_LARGE

                R.id.radio_grid_extra_large -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_GRID_EXTRA_LARGE

                R.id.radio_grid_small_no_image -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_GRID_SMALL_NO_IMAGE

                R.id.radio_grid_medium_no_image -> mGenericListenDataViewModel?.organizeListGrid?.value =
                    MainConst.ORGANIZE_GRID_MEDIUM_NO_IMAGE
            }
        }
    }

    private fun initViews() {
        updateTitleUI()
        updateCheckedOrganizeButtonUI()
    }

    private fun updateTitleUI() {
        when (mFromSource) {
            AllSongsFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.all_songs) ?: ""
                ) ?: ""
            }

            AlbumsFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_album) ?: ""
                ) ?: ""
            }

            AlbumArtistsFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_album_artist) ?: ""
                ) ?: ""
            }

            ArtistsFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_artist) ?: ""
                ) ?: ""
            }

            ComposersFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_composer) ?: ""
                ) ?: ""
            }

            FoldersFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_folder) ?: ""
                ) ?: ""
            }

            GenresFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_genre) ?: ""
                ) ?: ""
            }

            YearsFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_year) ?: ""
                ) ?: ""
            }

            PlaylistsFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_playlist) ?: ""
                ) ?: ""
            }
//                    FavoritesFragment.TAG -> {
//                        mDataBinding?.textOrganizeDetails?.text = context?.resources?.getString(
//                            R.string._organize_for,
//                            context?.resources?.getString(R.string.songs_for_favorites) ?: ""
//                        ) ?: ""
//                    }
            StreamsFragment.TAG -> {
                mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                    R.string._organize_for,
                    context?.resources?.getString(R.string.songs_for_stream) ?: ""
                ) ?: ""
            }

            ExploreContentForFragment.TAG -> {
                when (mFromSourceValue) {
                    AlbumsFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_album) ?: ""
                        ) ?: ""
                    }

                    AlbumArtistsFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_album_artist) ?: ""
                        ) ?: ""
                    }

                    ArtistsFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_artist) ?: ""
                        ) ?: ""
                    }

                    ComposersFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_composer) ?: ""
                        ) ?: ""
                    }

                    FoldersFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_folder) ?: ""
                        ) ?: ""
                    }

                    GenresFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_genre) ?: ""
                        ) ?: ""
                    }

                    YearsFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_year) ?: ""
                        ) ?: ""
                    }

                    PlaylistsFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_playlist) ?: ""
                        ) ?: ""
                    }
//                    FavoritesFragment.TAG -> {
//                        mDataBinding?.textSortDetails?.text = context?.resources?.getString(
//                            R.string._organize_for,
//                            context?.resources?.getString(R.string.songs_for_favorites) ?: ""
//                        ) ?: ""
//                    }
                    StreamsFragment.TAG -> {
                        mDataBinding.textOrganizeDetails.text = context?.resources?.getString(
                            R.string._organize_for,
                            context?.resources?.getString(R.string.songs_for_stream) ?: ""
                        ) ?: ""
                    }
                }
            }
        }
    }

    private fun updateCheckedOrganizeButtonUI() {
        if (mGenericListenDataViewModel == null) return
        val tempOrganize: Int = mGenericListenDataViewModel?.organizeListGrid?.value ?: return
        mDataBinding.run {
            when (tempOrganize) {
                MainConst.ORGANIZE_LIST_EXTRA_SMALL -> mDataBinding.radioGroupOrganize.check(radioListExtraSmall.id)
                MainConst.ORGANIZE_LIST_SMALL -> mDataBinding.radioGroupOrganize.check(radioListSmall.id)
                MainConst.ORGANIZE_LIST_MEDIUM -> mDataBinding.radioGroupOrganize.check(radioListMedium.id)
                MainConst.ORGANIZE_LIST_LARGE -> mDataBinding.radioGroupOrganize.check(radioListLarge.id)
                MainConst.ORGANIZE_LIST_SMALL_NO_IMAGE -> mDataBinding.radioGroupOrganize.check(radioListSmallNoImage.id)
                MainConst.ORGANIZE_LIST_MEDIUM_NO_IMAGE -> mDataBinding.radioGroupOrganize.check(radioListMediumNoImage.id)
                MainConst.ORGANIZE_LIST_LARGE_NO_IMAGE -> mDataBinding.radioGroupOrganize.check(radioListLargeNoImage.id)
                MainConst.ORGANIZE_GRID_EXTRA_SMALL -> mDataBinding.radioGroupOrganize.check(radioGridExtraSmall.id)
                MainConst.ORGANIZE_GRID_SMALL -> mDataBinding.radioGroupOrganize.check(radioGridSmall.id)
                MainConst.ORGANIZE_GRID_MEDIUM -> mDataBinding.radioGroupOrganize.check(radioGridMedium.id)
                MainConst.ORGANIZE_GRID_LARGE -> mDataBinding.radioGroupOrganize.check(radioGridLarge.id)
                MainConst.ORGANIZE_GRID_EXTRA_LARGE -> mDataBinding.radioGroupOrganize.check(radioGridExtraLarge.id)
                MainConst.ORGANIZE_GRID_SMALL_NO_IMAGE -> mDataBinding.radioGroupOrganize.check(radioGridSmallNoImage.id)
                MainConst.ORGANIZE_GRID_MEDIUM_NO_IMAGE -> mDataBinding.radioGroupOrganize.check(radioGridMediumNoImage.id)
                else -> mDataBinding.radioGroupOrganize.check(radioListSmall.id)
            }
        }
    }

    fun updateBottomSheetData(
        genericListenDataViewModel: GenericListenDataViewModel?,
        fromSource: String?,
        fromSourceValue: String?
    ) {
        mGenericListenDataViewModel = genericListenDataViewModel
        mFromSource = fromSource
        mFromSourceValue = fromSourceValue
        initViews()
    }

    companion object {
        const val TAG = "OrganizeItemBottomSheetDialogFragment"

        fun getSpanCount(ctx: Context, organizeValue: Int?): Int {
            return when (organizeValue ?: MainConst.ORGANIZE_LIST_MEDIUM) {
                MainConst.ORGANIZE_GRID_SMALL_NO_IMAGE -> ctx.resources.getInteger(R.integer.organize_grid_no_image_span_count)
                MainConst.ORGANIZE_GRID_MEDIUM_NO_IMAGE -> ctx.resources.getInteger(R.integer.organize_grid_no_image_span_count)
                MainConst.ORGANIZE_GRID_EXTRA_SMALL -> ctx.resources.getInteger(R.integer.organize_grid_extra_small_span_count)
                MainConst.ORGANIZE_GRID_SMALL -> ctx.resources.getInteger(R.integer.organize_grid_small_span_count)
                MainConst.ORGANIZE_GRID_MEDIUM -> ctx.resources.getInteger(R.integer.organize_grid_medium_span_count)
                MainConst.ORGANIZE_GRID_LARGE -> ctx.resources.getInteger(R.integer.organize_grid_large_span_count)
                MainConst.ORGANIZE_GRID_EXTRA_LARGE -> ctx.resources.getInteger(R.integer.organize_grid_extra_large_span_count)
                else -> 1
            }
        }

        @JvmStatic
        fun newInstance() =
            OrganizeItemBottomSheetDialogFragment().apply {}
    }

    override fun onShow(dialog: DialogInterface?) {}
}