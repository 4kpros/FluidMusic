package com.prosabdev.fluidmusic.ui.activities.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.FolderUriTreeAdapter
import com.prosabdev.fluidmusic.databinding.ActivityStorageAccessSettingsBinding
import com.prosabdev.fluidmusic.models.FolderUriTree
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.DeviceInfoAndParserUtils
import com.prosabdev.fluidmusic.utils.ViewInsetModifiersUtils
import com.prosabdev.fluidmusic.viewmodels.activities.ActivityViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.activities.StorageAccessActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.models.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.models.ModelsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@BuildCompat.PrereleaseSdkCheck class StorageAccessSettingsActivity : AppCompatActivity() {

    private lateinit var mActivityStorageAccessSettingsBinding: ActivityStorageAccessSettingsBinding

    private lateinit var mFolderUriTreeViewModel: FolderUriTreeViewModel
    private lateinit var mStorageAccessActivityViewModel: StorageAccessActivityViewModel

    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mFolderUriTreeAdapter: FolderUriTreeAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityStorageAccessSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_storage_access_settings)

        initViews()
        MainScope().launch {
            setupAdapter()
            observeLiveData()
            checkInteractions()
            registerOnBackPressedCallback()
        }
    }

    private fun registerOnBackPressedCallback() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                MainScope().launch {
                    alertUriChangedBeforeExit()
                }
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    MainScope().launch {
                        alertUriChangedBeforeExit()
                    }
                }
            })
        }
    }

    private suspend fun alertUriChangedBeforeExit() {
        withContext(Dispatchers.IO){
            supportFinishAfterTransition()
        }
    }

    private suspend fun observeLiveData() {
        mFolderUriTreeViewModel.getAll()?.observe(this as LifecycleOwner){
            updateFolderUriTreesUI(it)
        }
        mStorageAccessActivityViewModel.getRemoveAllFoldersCounter().observe(this){
            MainScope().launch {
                removeAllFolders(it)
            }
        }
    }

    private fun removeAllFolders(i: Int) {
        if(i <= 0)
            return
        mFolderUriTreeAdapter?.submitList(ArrayList())
        lifecycleScope.launch(Dispatchers.IO){
            mFolderUriTreeViewModel.deleteAll()
        }
    }

    private fun updateFolderUriTreesUI(folderUriTrees: List<FolderUriTree>) {
        MainScope().launch {
            Log.i(ConstantValues.TAG, "Add URI : ${folderUriTrees.size}")
            mFolderUriTreeAdapter?.submitList(folderUriTrees)
            mActivityStorageAccessSettingsBinding.foldersCounter = folderUriTrees.size
        }
    }

    private fun checkInteractions() {
        mActivityStorageAccessSettingsBinding.buttonAddFolder.setOnClickListener{
            requestNewFolderFromSAF()
        }
        mActivityStorageAccessSettingsBinding.topAppBar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        mActivityStorageAccessSettingsBinding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_remove_all -> {
                    showBottomSheetDialog()
                }
            }
            true
        }
    }

    private fun showBottomSheetDialog() {
//        StorageAccessFullBottomSheetDialog(mStorageAccessActivityViewModel).show(supportFragmentManager, StorageAccessFullBottomSheetDialog.TAG)
    }

    private fun requestNewFolderFromSAF() {
        mOpenSAFDocumentTreeLauncher.launch(null)
    }

    private fun setupAdapter() {
        val spanCount = 1
        mFolderUriTreeAdapter = FolderUriTreeAdapter(object : FolderUriTreeAdapter.OnItemClickListener{
            override fun onRemoveFolder(position: Int) {
                onShowRemoveFolderDialog(position)
            }
        })
        val emptyList: ArrayList<String> = ArrayList()
        emptyList.add("")
        mEmptyBottomAdapter = EmptyBottomAdapter(emptyList)

        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(mFolderUriTreeAdapter!!)
        concatAdapter.addAdapter(mEmptyBottomAdapter!!)
        mActivityStorageAccessSettingsBinding.recyclerView.adapter = concatAdapter

        mLayoutManager = GridLayoutManager(this.baseContext, spanCount, GridLayoutManager.VERTICAL, false)
        mActivityStorageAccessSettingsBinding.recyclerView.layoutManager = mLayoutManager

    }
    private fun onShowRemoveFolderDialog(position: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Remove this folder ${mFolderUriTreeAdapter?.currentList?.get(position)?.path} ?")
            .setMessage("This folder will be removed from your accessible files. And also all song currently on this folder will be removed from all playlists")
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Remove") { dialog, _ ->
                lifecycleScope.launch(Dispatchers.Default){
                    removeFolderUriTree(mFolderUriTreeAdapter?.currentList?.get(position))
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun initViews() {
        mFolderUriTreeViewModel = ModelsViewModelFactory(this.baseContext).create(FolderUriTreeViewModel::class.java)
        mStorageAccessActivityViewModel = ActivityViewModelFactory().create(
            StorageAccessActivityViewModel::class.java)

        ViewInsetModifiersUtils.updateTopViewInsets(mActivityStorageAccessSettingsBinding.coordinatorLayout)
        ViewInsetModifiersUtils.updateBottomViewInsets(mActivityStorageAccessSettingsBinding.linearButtonsContainer)
    }

    private var treeUri: String?
        get() = PreferenceManager.getDefaultSharedPreferences(this).getString("tree_uri", null)
        set(value) {
            PreferenceManager.getDefaultSharedPreferences(this).edit {
                putString("tree_uri", value)
            }
        }
    private val mOpenSAFDocumentTreeLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            treeUri = uri.toString()
            val takeFlags: Int =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            this.contentResolver.takePersistableUriPermission(uri, takeFlags)

            MainScope().launch {
                addToFolderList(DeviceInfoAndParserUtils.formatAndReturnFolderUriSAF(this@StorageAccessSettingsActivity, uri))
            }
        }
    }
    private suspend fun addToFolderList(it: FolderUriTree?) {
        if(!isFolderSAFExist(it)){
            if(it != null){
                withContext(Dispatchers.IO){
                    mFolderUriTreeViewModel.insert(it)
                }
            }
        }else{
            MainScope().launch {
                Toast.makeText(this@StorageAccessSettingsActivity, "This folder have already been added !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isFolderSAFExist(folderUriTree : FolderUriTree?): Boolean {
        if((mFolderUriTreeAdapter?.currentList?.size ?: 0) > 0 && folderUriTree != null){
            val listSize : Int = mFolderUriTreeAdapter?.currentList?.size ?: 0
            for(i in listSize - 1 downTo  0){
                val tempData : FolderUriTree? = mFolderUriTreeAdapter?.currentList?.get(i)
                if(tempData != null){
                    if(
                        tempData.normalizeScheme.toString() == folderUriTree.normalizeScheme.toString() ||
                        (
                                (
                                        tempData.normalizeScheme.toString().contains(folderUriTree.normalizeScheme.toString()) ||
                                                folderUriTree.normalizeScheme.toString().contains(tempData.normalizeScheme.toString())
                                        )
                                        &&
                                        tempData.pathTree.toString() != folderUriTree.pathTree.toString()
                                )
                    ) {
                        if(tempData.normalizeScheme.toString().length > folderUriTree.normalizeScheme.toString().length){
                            removeFolderUriTree(tempData)
                        }else{
                            return true
                        }
                    }
                }
            }
        }
        return false
    }
    private fun removeFolderUriTree(folderUriTree: FolderUriTree?) {
        lifecycleScope.launch(Dispatchers.IO){
            mFolderUriTreeViewModel.delete(folderUriTree)
        }
    }
}