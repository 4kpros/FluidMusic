package com.prosabdev.fluidmusic.ui.fragments.actions

import android.support.v4.media.session.PlaybackStateCompat
import com.prosabdev.common.constants.WorkManagerConst
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.ui.fragments.ExploreContentsForFragment
import com.prosabdev.fluidmusic.ui.fragments.PlaylistsFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.MainFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.NowPlayingFragmentViewModel

abstract class PlaybackActions {
    companion object{
        fun getModelTypeInfo(
            mainFragmentViewModel: MainFragmentViewModel
        ): List<String> {
            val modelTypeInfo = ArrayList<String>()
            when (mainFragmentViewModel.currentSelectablePage.value) {
                AllSongsFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.songitem.SongItem.TAG)
                    modelTypeInfo.add("")
                    modelTypeInfo.add("")
                }
                ExploreContentsForFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.songitem.SongItem.TAG)
                    modelTypeInfo.add("")
                    modelTypeInfo.add("")
                }
                AlbumsFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.view.AlbumItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.view.AlbumItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                ArtistsFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.view.ArtistItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.view.ArtistItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                FoldersFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.view.FolderItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.view.FolderItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                GenresFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.view.GenreItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.view.GenreItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                AlbumArtistsFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.view.AlbumArtistItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.view.AlbumArtistItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                ComposersFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.view.ComposerItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.view.ComposerItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                YearsFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.view.YearItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.view.YearItem.INDEX_COLUM_TO_SONG_ITEM)
                }
                PlaylistsFragment.TAG -> {
                    modelTypeInfo.add(com.prosabdev.common.models.playlist.PlaylistItem.TAG)
                    modelTypeInfo.add(WorkManagerConst.ITEM_LIST_WHERE_EQUAL)
                    modelTypeInfo.add(com.prosabdev.common.models.playlist.PlaylistItem.INDEX_COLUM_TO_SONG_ITEM)
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
            nowPlayingFragmentViewModel : NowPlayingFragmentViewModel?,
            songItem: com.prosabdev.common.models.songitem.SongItem?
        ): Boolean {
            if(nowPlayingFragmentViewModel == null) return false
            if(songItem?.uri == null || songItem.uri?.isEmpty() ?: return false) return false

            nowPlayingFragmentViewModel.setCurrentPlayingSong(songItem)
            nowPlayingFragmentViewModel.setIsPlaying(true)
            nowPlayingFragmentViewModel.setPlayingProgressValue(0)
            return true
        }

        fun playSongAtPositionFromGenericAdapterView(
            nowPlayingFragmentViewModel : NowPlayingFragmentViewModel?,
            genericListenDataViewModel: GenericListenDataViewModel?,
            genericListGridItemAdapter: GenericListGridItemAdapter?,
            loadFromSource: String,
            loadFromSourceColumnIndex: String?,
            loadFromSourceColumnValue: String?,
            position: Int,
            repeat: Int? = null,
            shuffle: Int? = null
        ): Boolean {
            if(nowPlayingFragmentViewModel == null) return false
            if(genericListenDataViewModel == null) return false
            if(genericListGridItemAdapter == null || genericListGridItemAdapter.currentList.size <= 0) return false
            if(
                nowPlayingFragmentViewModel.getSortBy().value != genericListenDataViewModel.getSortBy().value ||
                nowPlayingFragmentViewModel.getIsInverted().value != genericListenDataViewModel.getIsInverted().value ||
                nowPlayingFragmentViewModel.getQueueListSource().value != loadFromSource ||
                nowPlayingFragmentViewModel.getQueueListSourceColumnIndex().value != loadFromSourceColumnIndex ||
                nowPlayingFragmentViewModel.getQueueListSourceColumnValue().value != loadFromSourceColumnValue
            ){
                nowPlayingFragmentViewModel.setSortBy(genericListenDataViewModel.getSortBy().value ?: "")
                nowPlayingFragmentViewModel.setIsInverted(genericListenDataViewModel.getIsInverted().value ?: false)
                nowPlayingFragmentViewModel.setQueueListSource(loadFromSource)
                nowPlayingFragmentViewModel.setQueueListSourceColumnIndex(loadFromSourceColumnIndex)
                nowPlayingFragmentViewModel.setQueueListSourceColumnValue(loadFromSourceColumnValue)

                nowPlayingFragmentViewModel.setUpdatePlaylistCounter()
            }
            nowPlayingFragmentViewModel.setIsPlaying(true)
            nowPlayingFragmentViewModel.setPlayingProgressValue(0)
            nowPlayingFragmentViewModel.setRepeat(repeat ?: nowPlayingFragmentViewModel.getRepeat().value ?: PlaybackStateCompat.REPEAT_MODE_NONE)
            nowPlayingFragmentViewModel.setShuffle(shuffle ?: nowPlayingFragmentViewModel.getShuffle().value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE)
            nowPlayingFragmentViewModel.setCurrentPlayingSong(getCurrentPlayingSongFromPosition(genericListGridItemAdapter, position))
            return true
        }
        private fun getCurrentPlayingSongFromPosition(
            genericListGridItemAdapter: GenericListGridItemAdapter?,
            position: Int
        ): com.prosabdev.common.models.songitem.SongItem? {
            if(genericListGridItemAdapter == null) return null
            if (position < 0 || position >= (genericListGridItemAdapter.currentList.size)) return null
            val tempSongItem: com.prosabdev.common.models.songitem.SongItem = genericListGridItemAdapter.currentList[position] as com.prosabdev.common.models.songitem.SongItem? ?: return null
            tempSongItem.position = position
            return tempSongItem
        }
    }
}