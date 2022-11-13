package com.prosabdev.fluidmusic.viewmodels

import android.app.Activity
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.generic.GenericSongItemDataListViewModel

class PermissionsFragmentViewModel : ViewModel() {

    private val mMutableHaveStorageAccess = MutableLiveData<Boolean>(false)

    private val mHaveStorageAccess: LiveData<Boolean> get() = mMutableHaveStorageAccess

    fun setHaveStorageAccess(value : Boolean){
        mMutableHaveStorageAccess.value = value
    }
    fun getHaveStorageAccess(): LiveData<Boolean> {
        return mHaveStorageAccess
    }
}