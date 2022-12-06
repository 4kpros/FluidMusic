package com.prosabdev.fluidmusic.ui.fragments.commonmethods

import android.support.v4.media.session.PlaybackStateCompat
import com.prosabdev.fluidmusic.adapters.generic.GenericListGridItemAdapter
import com.prosabdev.fluidmusic.models.PlaySongAtRequest
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.fragments.GenericListenDataViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.PlayerFragmentViewModel

abstract class FragmentCommonMediaPlaybackAction {
    companion object{

        fun playSongAtPosition(
            playerFragmentViewModel : PlayerFragmentViewModel?,
            genericListenDataViewModel: GenericListenDataViewModel?,
            genericListGridItemAdapter: GenericListGridItemAdapter?,
            position: Int,
            requestFromQueueMusic: Boolean = false,
            repeat: Int? = null,
            shuffle: Int? = null
        ): Boolean {
            if(playerFragmentViewModel == null) return false
            if(requestFromQueueMusic){
                val playSongAtRequest = PlaySongAtRequest()
                playSongAtRequest.position = position
                playSongAtRequest.playDirectly = true
                playSongAtRequest.playProgress = 0
                playSongAtRequest.repeat = repeat
                playSongAtRequest.shuffle = shuffle
                playerFragmentViewModel.setRequestPlaySongAt(playSongAtRequest)
            }
            //
            if(genericListenDataViewModel == null) return false
            if(genericListGridItemAdapter == null || genericListGridItemAdapter.currentList.size <= 0) return false
            if(
                playerFragmentViewModel.getSortBy().value != genericListenDataViewModel.getSortBy().value ||
                playerFragmentViewModel.getIsInverted().value != genericListenDataViewModel.getIsInverted().value ||
                playerFragmentViewModel.getQueueListSource().value != ConstantValues.EXPLORE_ALL_SONGS
            ){
                playerFragmentViewModel.setQueueListSource(ConstantValues.EXPLORE_ALL_SONGS)
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