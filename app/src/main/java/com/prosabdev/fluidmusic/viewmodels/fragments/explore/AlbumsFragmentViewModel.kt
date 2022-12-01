package com.prosabdev.fluidmusic.viewmodels.fragments.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prosabdev.fluidmusic.models.view.AlbumItem
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.viewmodels.models.explore.AlbumItemViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlbumsFragmentViewModel(app: Application) : AndroidViewModel(app) {
    private val mMutableSongList = MutableLiveData<ArrayList<AlbumItem>>(null)
    private val mMutableSortBy = MutableLiveData<String>("name")
    private val mMutableOrganizeListGrid = MutableLiveData<Int>(ConstantValues.ORGANIZE_LIST)
    private val mMutableIsInverted = MutableLiveData<Boolean>(false)

    private val mSongList: LiveData<ArrayList<AlbumItem>> get() = mMutableSongList
    private val mSortBy: LiveData<String> get() = mMutableSortBy
    private val mOrganizeListGrid: LiveData<Int> get() = mMutableOrganizeListGrid
    private val mIsInverted: LiveData<Boolean> get() = mMutableIsInverted

    fun listenAllAlbums(albumItemViewModel: AlbumItemViewModel, lifecycleOwner: LifecycleOwner){
        MainScope().launch {
            albumItemViewModel.getAll(mSortBy.value ?: "name")?.observe(lifecycleOwner){
                mMutableSongList.value = it as ArrayList<AlbumItem>?
            }
        }
    }
    fun getAll(): LiveData<ArrayList<AlbumItem>> {
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