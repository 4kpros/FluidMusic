package com.prosabdev.fluidmusic.viewmodels

import android.content.Context
import com.prosabdev.common.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.common.sharedprefs.models.SortOrganizeItemSP
import com.prosabdev.common.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel

abstract class SortOrganizePrefsLoaderAndSetupViewModels {
    companion object {
        fun loadSortOrganizeItemsSettings(
            ctx: Context,
            genericListenDataViewModel : GenericListenDataViewModel,
            sharedPrefsKey: String
        ) {
            var defaultOrderBy: String = "id"
            var defaultOrganizeListGrid: Int = 1
            if(
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_ALL_SONGS ||

                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_FOLDER_HIERARCHY_MUSIC_CONTENT ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_PLAYLIST_MUSIC_CONTENT ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_STREAM_MUSIC_CONTENT ||

                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE ||
                sharedPrefsKey == SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_YEAR
            ){
                defaultOrderBy = "title"
                defaultOrganizeListGrid = ConstantValues.ORGANIZE_LIST_SMALL
            }else {
                defaultOrderBy = "name"
                defaultOrganizeListGrid = ConstantValues.ORGANIZE_GRID_MEDIUM
            }
            setupViewModelSortOrganizeFor(
                ctx,
                genericListenDataViewModel,
                sharedPrefsKey,
                defaultOrderBy,
                defaultOrganizeListGrid,
                false
            )
        }

        private fun setupViewModelSortOrganizeFor(
            ctx: Context,
            genericListenDataViewModel : GenericListenDataViewModel,
            sharedPrefsKey: String,
            defaultOrderBy: String,
            defaultOrganize: Int,
            defaultIsInverted: Boolean
        ) {
            val sortOrganize: SortOrganizeItemSP? =
                SharedPreferenceManagerUtils.SortAnOrganizeForExploreContents.loadSortOrganizeItemsFor(
                    ctx,
                    sharedPrefsKey
                )
            genericListenDataViewModel.setSortBy(sortOrganize?.sortOrderBy ?: defaultOrderBy)
            genericListenDataViewModel.setOrganizeListGrid(sortOrganize?.organizeListGrid ?: defaultOrganize)
            genericListenDataViewModel.setIsInverted(sortOrganize?.isInvertSort ?: defaultIsInverted)

        }
    }
}