package com.prosabdev.fluidmusic.viewmodels.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.prosabdev.common.models.FolderUriTree
import com.prosabdev.common.roomdatabase.repositories.FolderUriTreeRepository

class FolderUriTreeViewModel(app: Application) : AndroidViewModel(app) {

    private var mRepository: FolderUriTreeRepository? = FolderUriTreeRepository(app)

    suspend fun insert(folderUriTree: FolderUriTree?) : Long? {
        return mRepository?.insert(folderUriTree)
    }
    suspend fun update(folderUriTree: FolderUriTree?) {
        mRepository?.update(folderUriTree)
    }
    suspend fun delete(folderUriTree: FolderUriTree?) {
        mRepository?.delete(folderUriTree)
    }
    suspend fun deleteAll() {
        mRepository?.deleteAll()
    }

    //Getters
    suspend fun getAtId(id: Long) : FolderUriTree? {
        return mRepository?.getAtId(id)
    }
    suspend fun getAll(orderBy: String = "id") : LiveData<List<FolderUriTree>>? {
        return mRepository?.getAll(orderBy)
    }
}