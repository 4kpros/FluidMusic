package com.prosabdev.fluidmusic.ui.fragments.commonmethods

import android.support.v4.media.session.PlaybackStateCompat
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.models.playlist.PlaylistItem
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.models.view.*
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.ui.fragments.PlaylistsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel
import com.prosabdev.fluidmusic.workers.WorkerConstantValues

abstract class CommonPlaybackAction {
    companion object{
        fun getModelTypeInfo(
            mainFragmentViewModel: MainFragmentViewModel
        ): List<String> {
            val modelTypeInfo = ArrayList<String>()
            when (mainFragmentViewModel.getCurrentSelectablePage().value) {
                AllSongsFragment.TAG -> {
                    modelTypeInfo.add(SongItem.TAG)
                    modelTypeInfo.add("")
                    modelTypeInfo.add("")
                }
                ExploreContentsForFragment.TAG -> {
                    modelTypeInfo.add(SongItem.TAG)
                    modelTypeInfo.add("")
                    modelTypeInfo.add("")
                }
                AlbumsFragment.TAG -> {
                    modelTypeInfo.add(AlbumItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(AlbumItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                ArtistsFragment.TAG -> {
                    modelTypeInfo.add(ArtistItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(ArtistItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                FoldersFragment.TAG -> {
                    modelTypeInfo.add(FolderItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(FolderItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                GenresFragment.TAG -> {
                    modelTypeInfo.add(GenreItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(GenreItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                AlbumArtistsFragment.TAG -> {
                    modelTypeInfo.add(AlbumArtistItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(AlbumArtistItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                ComposersFragment.TAG -> {
                    modelTypeInfo.add(ComposerItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(ComposerItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                YearsFragment.TAG -> {
                    modelTypeInfo.add(YearItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(YearItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                PlaylistsFragment.TAG -> {
                    modelTypeInfo.add(PlaylistItem.TAG)
                    modelTypeInfo.add(WorkerConstantValues.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(PlaylistItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                else -> {
                    modelTypeInfo.add("")
                    modelTypeInfo.add("")
                    modelTypeInfo.add("")
                }
            }
            return modelTypeInfo
        }

        fun playSongAtPositionFromQueueMusic(
            playerFragmentViewModel : PlayerFragmentViewModel?,
            songItem: SongItem?
        ): Boolean {
            if(playerFragmentViewModel == null) return false
            if(songItem?.uri == null || songItem.uri?.isEmpty() ?: return false) return false

            playerFragmentViewModel.setCurrentPlayingSong(songItem)
            playerFragmentViewModel.setIsPlaying(true)
            playerFragmentViewModel.setPlayingProgressValue(0)
            return true
        }

        fun playSongAtPositionFromGenericAdapterView(
            playerFragmentViewModel : PlayerFragmentViewModel?,
            genericListenDataViewModel: GenericListenDataViewModel?,
            genericListGridItemAdapter: GenericListGridItemAdapter?,
            loadFromSource: String,
            loadFromSourceColumnIndex: String?,
            loadFromSourceColumnValue: String?,
            position: Int,
            repeat: Int? = null,
            shuffle: Int? = null
        ): Boolean {
            if(playerFragmentViewModel == null) return false
            if(genericListenDataViewModel == null) return false
            if(genericListGridItemAdapter == null || genericListGridItemAdapter.currentList.size <= 0) return false
            if(
                playerFragmentViewModel.getSortBy().value != genericListenDataViewModel.getSortBy().value ||
                playerFragmentViewModel.getIsInverted().value != genericListenDataViewModel.getIsInverted().value ||
                playerFragmentViewModel.getQueueListSource().value != loadFromSource ||
                playerFragmentViewModel.getQueueListSourceColumnIndex().value != loadFromSourceColumnIndex ||
                playerFragmentViewModel.getQueueListSourceColumnValue().value != loadFromSourceColumnValue
            ){
                playerFragmentViewModel.setSortBy(genericListenDataViewModel.getSortBy().value ?: "")
                playerFragmentViewModel.setIsInverted(genericListenDataViewModel.getIsInverted().value ?: false)
                playerFragmentViewModel.setQueueListSource(loadFromSource)
                playerFragmentViewModel.setQueueListSourceColumnIndex(loadFromSourceColumnIndex)
                playerFragmentViewModel.setQueueListSourceColumnValue(loadFromSourceColumnValue)

                playerFragmentViewModel.setUpdatePlaylistCounter()
            }
            playerFragmentViewModel.setIsPlaying(true)
            playerFragmentViewModel.setPlayingProgressValue(0)
            playerFragmentViewModel.setRepeat(repeat ?: playerFragmentViewModel.getRepeat().value ?: PlaybackStateCompat.REPEAT_MODE_NONE)
            playerFragmentViewModel.setShuffle(shuffle ?: playerFragmentViewModel.getShuffle().value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE)
            playerFragmentViewModel.setCurrentPlayingSong(getCurrentPlayingSongFromPosition(genericListGridItemAdapter, position))
            return true
        }
        private fun getCurrentPlayingSongFromPosition(
            genericListGridItemAdapter: GenericListGridItemAdapter?,
            position: Int
        ): SongItem? {
            if(genericListGridItemAdapter == null) return null
            if (position < 0 || position >= (genericListGridItemAdapter.currentList.size)) return null
            val tempSongItem: SongItem = genericListGridItemAdapter.currentList[position] as SongItem? ?: return null
            tempSongItem.position = position
            return tempSongItem
        }
    }
}