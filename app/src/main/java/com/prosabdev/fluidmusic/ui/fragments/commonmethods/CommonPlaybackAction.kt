package com.prosabdev.fluidmusic.ui.fragments.commonmethods

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.util.SparseBooleanArray
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.models.songitem.SongItem
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel

abstract class CommonPlaybackAction {
    companion object{

        fun playSongAtPositionFromQueueMusic(
            playerFragmentViewModel : PlayerFragmentViewModel?,
            songItem: SongItem?
        ): Boolean {
            if(playerFragmentViewModel == null) return false
            if(songItem?.uri == null || songItem.uri?.isEmpty() == true) return false

            playerFragmentViewModel.setCurrentPlayingSong(songItem)
            playerFragmentViewModel.setIsPlaying(true)
            playerFragmentViewModel.setPlayingProgressValue(0)
            return true
        }

        fun playSongAtPositionFromGenericAdapterView(
            playerFragmentViewModel : PlayerFragmentViewModel?,
            genericListenDataViewModel: GenericListenDataViewModel?,
            genericListGridItemAdapter: GenericListGridItemAdapter?,
            fragmentSource: String,
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
                playerFragmentViewModel.getQueueListSource().value != fragmentSource
            ){
                playerFragmentViewModel.setQueueListSource(fragmentSource)
                playerFragmentViewModel.setUpdatePlaylistCounter()
            }
            playerFragmentViewModel.setCurrentPlayingSong(getCurrentPlayingSongFromPosition(genericListGridItemAdapter, position))
            playerFragmentViewModel.setIsPlaying(true)
            playerFragmentViewModel.setPlayingProgressValue(0)
            playerFragmentViewModel.setRepeat(repeat ?: playerFragmentViewModel.getRepeat().value ?: PlaybackStateCompat.REPEAT_MODE_NONE)
            playerFragmentViewModel.setShuffle(shuffle ?: playerFragmentViewModel.getShuffle().value ?: PlaybackStateCompat.SHUFFLE_MODE_NONE)
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