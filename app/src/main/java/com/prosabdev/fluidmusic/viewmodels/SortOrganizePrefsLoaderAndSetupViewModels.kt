package com.prosabdev.fluidmusic.viewmodels

import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.persistence.models.SortOrganizeItemSP
import com.prosabdev.common.constants.MainConst
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel

abstract class SortOrganizePrefsLoaderAndSetupViewModels {
    companion object {
        fun loadSortOrganizeItemsSettings(
            genericListenDataViewModel : GenericListenDataViewModel,
            sharedPrefsKey: String
        ) {
            var defaultOrderBy: String = "id"
            var defaultOrganizeListGrid: Int = 1
            if(
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_ALL_SONGS ||

                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_FOLDER_HIERARCHY_MUSIC_CONTENT ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_PLAYLIST_MUSIC_CONTENT ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_STREAM_MUSIC_CONTENT ||

                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM_ARTIST ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ALBUM ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_ARTIST ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_COMPOSER ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_FOLDER ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_GENRE ||
                sharedPrefsKey == PersistentStorage.SortAnOrganizeForExploreContents.SORT_ORGANIZE_EXPLORE_MUSIC_CONTENT_FOR_YEAR
            ){
                defaultOrderBy = "title"
                defaultOrganizeListGrid = MainConst.ORGANIZE_LIST_SMALL
            }else {
                defaultOrderBy = "name"
                defaultOrganizeListGrid = MainConst.ORGANIZE_GRID_MEDIUM
            }
            setupViewModelSortOrganizeFor(
                genericListenDataViewModel,
                sharedPrefsKey,
                defaultOrderBy,
                defaultOrganizeListGrid,
                false
            )
        }

        private fun setupViewModelSortOrganizeFor(
            genericListenDataViewModel : GenericListenDataViewModel,
            sharedPrefsKey: String,
            defaultOrderBy: String,
            defaultOrganize: Int,
            defaultIsInverted: Boolean
        ) {
            val sortOrganize: SortOrganizeItemSP? =
                PersistentStorage.SortAnOrganizeForExploreContents.loadSortOrganizeItemsFor(
                    sharedPrefsKey
                )
            genericListenDataViewModel.sortBy.value = sortOrganize?.sortOrderBy ?: defaultOrderBy
            genericListenDataViewModel.organizeListGrid.value = sortOrganize?.organizeListGrid ?: defaultOrganize
            genericListenDataViewModel.isInverted.value = sortOrganize?.isInvertSort ?: defaultIsInverted

        }
    }
}