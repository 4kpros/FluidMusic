package com.prosabdev.fluidmusic.viewmodels.fragments

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.prosabdev.common.components.Constants
import com.prosabdev.common.models.songitem.SongItem
import com.prosabdev.common.models.view.AlbumItem
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.persistence.models.SortOrganizeItemSP
import com.prosabdev.common.utils.AudioInfoExtractor
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AllSongsFragmentViewModel

abstract class GenericListenDataViewModel (private val app: Application) : AndroidViewModel(app) {
    val itemsList = MutableLiveData<List<Any>>(null)
    val sortBy = MutableLiveData<String>("")
    val organizeListGrid = MutableLiveData<Int>(Constants.ORGANIZE_LIST_MEDIUM)
    val isInverted = MutableLiveData<Boolean>(false)

    protected fun loadPreferences(
        prefsKey: String,
        defaultSortBy: String,
        defaultOrganizeListGrid: Int,
        defaultIsInverted: Boolean
    ) {
        val tempSortOrganize: SortOrganizeItemSP? =
            PersistentStorage
                .SortAndOrganize
                .load(
                    prefsKey
                )
        tempSortOrganize?.let {
            sortBy.value = it.sortOrderBy
            organizeListGrid.value = it.organizeListGrid
            isInverted.value = it.isInvertSort
        }
        if (tempSortOrganize == null) {
            sortBy.value = defaultSortBy
            organizeListGrid.value = defaultOrganizeListGrid
            isInverted.value = defaultIsInverted
        }
    }

    protected fun savePreferences(
        prefsKey: String,
        defaultSortBy: String,
        defaultOrganizeListGrid: Int,
        defaultIsInverted: Boolean
    ) {
        val tempSortOrganize = SortOrganizeItemSP()
        tempSortOrganize.sortOrderBy =
            sortBy.value ?: defaultSortBy
        tempSortOrganize.organizeListGrid =
            organizeListGrid.value ?: defaultOrganizeListGrid
        tempSortOrganize.isInvertSort =
            isInverted.value ?: defaultIsInverted
        PersistentStorage
            .SortAndOrganize
            .save(
                prefsKey,
                tempSortOrganize
            )
    }

    open fun getMediaItems(): List<MediaItem> {
        if ((itemsList.value?.size ?: 0) <= 0) return emptyList()

        val mediaItems: ArrayList<MediaItem> = arrayListOf()
        for (i in 0..<(itemsList.value?.size ?: 0)) {
            itemsList.value?.get(i)?.let { item ->
                when(item){
                    is SongItem -> {
                        mediaItems.add(SongItem.castSongItemToMediaItem(app, item))
                    }
                    else -> {}
                }
            }
        }

        return mediaItems
    }

    open fun getSelectedMediaItems(items: List<Any>?): List<MediaItem> {
        if ((items?.size ?: 0) <= 0) return emptyList()

        val mediaItems: ArrayList<MediaItem> = arrayListOf()
        for (i in 0..<(items?.size ?: 0)) {
            items?.get(i)?.let { item ->
                when(item){
                    is SongItem -> {
                        mediaItems.add(SongItem.castSongItemToMediaItem(app, item))
                    }
                    else -> {}
                }
            }
        }

        return mediaItems
    }
}
