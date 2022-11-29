package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.models.SongItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.models.explore.SongItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AllSongsFragmentViewModel : ViewModel() {
    private val mMutableSongList = MutableLiveData<ArrayList<SongItem>>(null)
    private val mMutableSortBy = MutableLiveData<String>("title")
    private val mMutableOrganizeListGrid = MutableLiveData<Int>(ConstantValues.ORGANIZE_LIST)
    private val mMutableIsInverted = MutableLiveData<Boolean>(false)

    private val mSongList: LiveData<ArrayList<SongItem>> get() = mMutableSongList
    private val mSortBy: LiveData<String> get() = mMutableSortBy
    private val mOrganizeListGrid: LiveData<Int> get() = mMutableOrganizeListGrid
    private val mIsInverted: LiveData<Boolean> get() = mMutableIsInverted

    fun requestSongAtId(songItemViewModel: SongItemViewModel, songId : Long){
        MainScope().launch {
            val tempSong : SongItem? = songItemViewModel.getAtId(songId)
            tempSong?.let { it ->
                val tempSongList: ArrayList<SongItem> = ArrayList()
                tempSongList.add(it)
                mMutableSongList.value = tempSongList
            }
        }
    }
    fun listenAllSongs(songItemViewModel: SongItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            songItemViewModel.getAll(mSortBy.value ?: "title")?.observe(lifecycleOwner){
                mMutableSongList.value = it as ArrayList<SongItem>?
            }
        }
    }
    fun getAllSongs(): LiveData<ArrayList<SongItem>> {
        return mSongList
    }
    fun setSortBy(sortBy : String) {
        MainScope().launch {
            mMutableSortBy.value = sortBy
        }
    }
    fun getSortBy(): LiveData<String> {
        return mSortBy
    }
    fun setOrganizeListGrid(organizeListGrid : Int) {
        MainScope().launch {
            mMutableOrganizeListGrid.value = organizeListGrid
        }
    }
    fun getOrganizeListGrid(): LiveData<Int> {
        return mOrganizeListGrid
    }
    fun setIsInverted(isInverted : Boolean) {
        MainScope().launch {
            mMutableIsInverted.value = isInverted
        }
    }
    fun getIsInverted(): LiveData<Boolean> {
        return mIsInverted
    }
}