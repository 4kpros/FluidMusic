package com.prosabdev.fluidmusic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemStorageAccessFolderBinding
import com.prosabdev.fluidmusic.models.FolderSAF


class StorageAccessAdapter(
    private val mFolderSAFList: ArrayList<FolderSAF>,
    private val mListener: OnItemClickListener
) : RecyclerView.Adapter<StorageAccessAdapter.FolderSelectionHolder>() {

    interface OnItemClickListener {
        fun onRemoveFolder(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderSelectionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_storage_access_folder, parent, false)

        val tempItemStorageAccessFolderBinding: ItemStorageAccessFolderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_storage_access_folder, parent, false
        )
        return FolderSelectionHolder(tempItemStorageAccessFolderBinding)
    }

    override fun onBindViewHolder(holder: FolderSelectionHolder, position: Int) {
        holder.bindData(mFolderSAFList[position], mListener, position)
    }

    override fun getItemCount(): Int {
        return mFolderSAFList.size
    }

    class FolderSelectionHolder(private val mItemStorageAccessFolderBinding: ItemStorageAccessFolderBinding) : RecyclerView.ViewHolder(mItemStorageAccessFolderBinding.root) {

        fun bindData(folderSAF: FolderSAF, mListener: OnItemClickListener, position: Int) {
            mItemStorageAccessFolderBinding.folderSAF = folderSAF
            mItemStorageAccessFolderBinding.itemClickListener = mListener
            mItemStorageAccessFolderBinding.position = position
            mItemStorageAccessFolderBinding.executePendingBindings()
            mItemStorageAccessFolderBinding.textFolder.isSelected = true
            mItemStorageAccessFolderBinding.textVolume.isSelected = true
        }
    }
}