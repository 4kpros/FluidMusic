package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemStorageAccessFolderBinding


class FolderUriTreeAdapter(
    private val mListener: OnItemClickListener
) : ListAdapter<com.prosabdev.common.models.FolderUriTree, FolderUriTreeAdapter.FolderUriTreeHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onRemoveFolder(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderUriTreeHolder {
        val tempItemStorageAccessFolderBinding: ItemStorageAccessFolderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_storage_access_folder, parent, false
        )
        return FolderUriTreeHolder(tempItemStorageAccessFolderBinding)
    }

    override fun onBindViewHolder(holder: FolderUriTreeHolder, position: Int) {
        holder.bindData(getItem(position), mListener, position)
    }

    class FolderUriTreeHolder(private val mItemStorageAccessFolderBinding: ItemStorageAccessFolderBinding) : RecyclerView.ViewHolder(mItemStorageAccessFolderBinding.root) {

        fun bindData(folderUriTree: com.prosabdev.common.models.FolderUriTree, mListener: OnItemClickListener, position: Int) {
            mItemStorageAccessFolderBinding.folderUriTree = folderUriTree
            mItemStorageAccessFolderBinding.executePendingBindings()
            mItemStorageAccessFolderBinding.textFolder.isSelected = true
            mItemStorageAccessFolderBinding.textVolume.isSelected = true
            mItemStorageAccessFolderBinding.buttonRemove.setOnClickListener{
                mListener.onRemoveFolder(position)
            }
        }
    }
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<com.prosabdev.common.models.FolderUriTree>() {
            override fun areItemsTheSame(oldItem: com.prosabdev.common.models.FolderUriTree, newItem: com.prosabdev.common.models.FolderUriTree): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: com.prosabdev.common.models.FolderUriTree, newItem: com.prosabdev.common.models.FolderUriTree): Boolean {
                return oldItem == newItem
            }
        }
    }
}