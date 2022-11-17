package com.prosabdev.fluidmusic.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ItemStorageAccessFolderBinding
import com.prosabdev.fluidmusic.models.FolderUriTree


class FolderUriTreeAdapter(
    private val mListener: OnItemClickListener
) : ListAdapter<FolderUriTree, FolderUriTreeAdapter.FolderSelectionHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onRemoveFolder(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderSelectionHolder {
        val tempItemStorageAccessFolderBinding: ItemStorageAccessFolderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_storage_access_folder, parent, false
        )
        return FolderSelectionHolder(tempItemStorageAccessFolderBinding)
    }

    override fun onBindViewHolder(holder: FolderSelectionHolder, position: Int) {
        holder.bindData(getItem(position), mListener, position)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    class FolderSelectionHolder(private val mItemStorageAccessFolderBinding: ItemStorageAccessFolderBinding) : RecyclerView.ViewHolder(mItemStorageAccessFolderBinding.root) {

        fun bindData(folderUriTree: FolderUriTree, mListener: OnItemClickListener, position: Int) {
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
        private val DiffCallback = object : DiffUtil.ItemCallback<FolderUriTree>() {
            override fun areItemsTheSame(oldItem: FolderUriTree, newItem: FolderUriTree): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: FolderUriTree, newItem: FolderUriTree): Boolean {
                return oldItem == newItem
            }
        }
    }
}