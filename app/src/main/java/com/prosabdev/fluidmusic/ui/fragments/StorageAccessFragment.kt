package com.prosabdev.fluidmusic.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.StorageAccessAdapter
import com.prosabdev.fluidmusic.databinding.FragmentStorageAccessBinding
import com.prosabdev.fluidmusic.models.FolderSAF
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.viewmodels.StorageAccessFragmentViewModel

class StorageAccessFragment : Fragment() {

    private lateinit var mFragmentStorageAccessBinding: FragmentStorageAccessBinding

    private val mStorageAccessFragmentViewModel: StorageAccessFragmentViewModel by activityViewModels()

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mStorageAccessAdapter: StorageAccessAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mFolderSelectionList : ArrayList<FolderSAF> = ArrayList<FolderSAF>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mFragmentStorageAccessBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_storage_access,container,false)
        val view = mFragmentStorageAccessBinding.root

        initViews()
        setupAdapter()
        checkInteractions()
        observeLiveData()

        return view
    }

    private fun observeLiveData() {
        mStorageAccessFragmentViewModel.getAddFolderSAF().observe(mActivity){
            addToFolderList(it)
        }
        mStorageAccessFragmentViewModel.getFoldersList().observe(mActivity){
            updateLoadingUI(it)
        }
    }

    private fun updateLoadingUI(folderSAFS: ArrayList<FolderSAF>) {
        Log.i(ConstantValues.TAG, "SIZE OF LIST = ${folderSAFS.size}")
        if(folderSAFS.size > 0){
            //Hide loading view
            if(mFragmentStorageAccessBinding.constraintLoadingContent.alpha == 1.0f){
                CustomAnimators.crossFadeDown(mFragmentStorageAccessBinding.hoverButtonSave, false)
                CustomAnimators.crossFadeDown(mFragmentStorageAccessBinding.constraintLoadingContent, false)
                CustomAnimators.crossFadeDown(mFragmentStorageAccessBinding.textLoadingDetails, false)
            }
        }else{
            //show loading view
            if(mFragmentStorageAccessBinding.hoverButtonSave.visibility != View.VISIBLE){
                CustomAnimators.crossFadeUp(mFragmentStorageAccessBinding.hoverButtonSave, false, 200, 0.8f)
                CustomAnimators.crossFadeUp(mFragmentStorageAccessBinding.constraintLoadingContent, false)
                CustomAnimators.crossFadeUp(mFragmentStorageAccessBinding.textLoadingDetails, false)
            }
        }
    }

    private fun addToFolderList(it: FolderSAF?) {
        if(it != null){
            mFolderSelectionList.add(it)
            mStorageAccessAdapter?.notifyItemInserted(mFolderSelectionList.size - 1)
            mStorageAccessFragmentViewModel.setAddFolderSAF(null)
        }
    }

    private fun checkInteractions() {
        mFragmentStorageAccessBinding.buttonAddFolder.setOnClickListener{
            requestNewFolderFromSAF()
        }
        mFragmentStorageAccessBinding.buttonNext.setOnClickListener{
            Log.i(ConstantValues.TAG, "On next")
        }
//        mFragmentStorageAccessBinding.topAppBar.action
    }
    private fun requestNewFolderFromSAF() {
        var tempReCounter = mStorageAccessFragmentViewModel.getRequestAddFolder().value ?: 0
        tempReCounter++
        mStorageAccessFragmentViewModel.setRequestAddFolder(tempReCounter)
    }

    private fun setupAdapter() {
        val spanCount = 1
        mStorageAccessAdapter = StorageAccessAdapter(mFolderSelectionList, object : StorageAccessAdapter.OnItemClickListener{
            override fun onRemoveFolder(position: Int) {
                onShowRemoveFolderDialog(position)
            }
        })
        val emptyList: ArrayList<String> = ArrayList()
        emptyList.add("Empty")
        mEmptyBottomAdapter = EmptyBottomAdapter(emptyList)

        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(mStorageAccessAdapter!!)
        concatAdapter.addAdapter(mEmptyBottomAdapter!!)
        mFragmentStorageAccessBinding.recyclerView.adapter = concatAdapter

        mLayoutManager = GridLayoutManager(mContext, spanCount, GridLayoutManager.VERTICAL, false)
        mFragmentStorageAccessBinding.recyclerView.layoutManager = mLayoutManager

    }
    private fun onShowRemoveFolderDialog(position: Int) {
        Log.i(ConstantValues.TAG, "On remove : $position")
        if (position >= (mStorageAccessFragmentViewModel.getFoldersList().value?.size ?: 0))
            return
        MaterialAlertDialogBuilder(mContext)
            .setTitle("Remove this folder ${mStorageAccessFragmentViewModel.getFoldersList().value?.get(position)?.path} ?")
            .setMessage("This folder will be removed from your accessible files. And also all song currently on this folder will be removed from all playlists")
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Remove") { dialog, which ->
                removeFolder(position)
            }
            .show()
    }
    private fun removeFolder(position: Int) {
        mFolderSelectionList.removeAt(position)
        mStorageAccessFragmentViewModel.removeFromFoldersList(position)
        mStorageAccessAdapter?.notifyItemRemoved(position)
        mStorageAccessAdapter?.notifyItemRangeChanged(position, mStorageAccessAdapter!!.itemCount)
    }

    private fun initViews() {
        CustomViewModifiers.updateTopViewInsets(mFragmentStorageAccessBinding.coordinator)
        CustomViewModifiers.updateBottomViewInsets(mFragmentStorageAccessBinding.constraintMainContainer)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            StorageAccessFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}