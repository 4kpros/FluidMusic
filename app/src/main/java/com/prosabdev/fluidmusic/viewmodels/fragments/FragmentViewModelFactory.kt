package com.prosabdev.fluidmusic.viewmodels.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.AlbumsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.ArtistsFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.FoldersFragmentViewModel
import com.prosabdev.fluidmusic.viewmodels.fragments.explore.GenresFragmentViewModel

class FragmentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainFragmentViewModel() as T
        }else if(modelClass.isAssignableFrom(PlayerFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerFragmentViewModel() as T
        }else if(modelClass.isAssignableFrom(AlbumsFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumsFragmentViewModel() as T
        }else if(modelClass.isAssignableFrom(ArtistsFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArtistsFragmentViewModel() as T
        }else if(modelClass.isAssignableFrom(FoldersFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoldersFragmentViewModel() as T
        }else if(modelClass.isAssignableFrom(GenresFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenresFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}