package com.prosabdev.fluidmusic.ui.activities.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
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
import com.prosabdev.fluidmusic.roomdatabase.bus.DatabaseAccessApplication
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.StorageAccessDialog
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.FolderUriTreeViewModel
import com.prosabdev.fluidmusic.viewmodels.FolderUriTreeViewModelFactory
import com.prosabdev.fluidmusic.viewmodels.views.activities.StorageAccessActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.views.activities.StorageAccessActivityViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }

    private fun observeLiveData() {
        lifecycle.coroutineScope.launch {
            mFolderUriTreeViewModel.getAllFolderUriTrees().collect {
                updateFolderUriTrees(it)
            }
        }
        mStorageAccessActivityViewModel.getRemoveAllFoldersCounter().observe(this){
            MainScope().launch {
                removeAllFolders(it)
            }
        }
    }

    private fun removeAllFolders(i: Int) {
        //Return if the value of delete all have not been incremented return
        if(i <= 0)
            return

        CoroutineScope(Dispatchers.IO).launch {
            mFolderUriTreeViewModel.deleteAll()
        }
    }

    private fun updateFolderUriTrees(folderUriTrees: List<FolderUriTree>) {
        mFolderUriTreeAdapter?.submitList(folderUriTrees)
        mActivityStorageAccessSettingsBinding.foldersCounter = folderUriTrees.size
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
        StorageAccessDialog(mStorageAccessActivityViewModel).show(supportFragmentManager, StorageAccessDialog.TAG)
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
            .setPositiveButton("Remove") { _, _ ->
                removeFolderUriTree(mFolderUriTreeAdapter?.currentList?.get(position))
            }
            .show()
    }

    private fun initViews() {
        mFolderUriTreeViewModel = FolderUriTreeViewModelFactory(
            (this.application as DatabaseAccessApplication).database.folderUriTreeDao()
        ).create(FolderUriTreeViewModel::class.java)
        mStorageAccessActivityViewModel = StorageAccessActivityViewModelFactory().create(StorageAccessActivityViewModel::class.java)

        CustomViewModifiers.updateTopViewInsets(mActivityStorageAccessSettingsBinding.coordinatorSettingsActivity)
        CustomViewModifiers.updateBottomViewInsets(mActivityStorageAccessSettingsBinding.linearButtonsContainer)
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

            lifecycleScope.launch(context = Dispatchers.Default) {
                addToFolderList(MediaFileScanner.formatAndReturnFolderUriSAF(this@StorageAccessSettingsActivity, uri))
            }
        }
    }
    private suspend fun addToFolderList(it: FolderUriTree?) {
        if(!isFolderSAFExist(it)){
            if(it != null){
                lifecycleScope.launch(context = Dispatchers.IO){
                    mFolderUriTreeViewModel.insertFolderUriTree(it)
                }
            }
        }else{
            MainScope().launch {
                Toast.makeText(this@StorageAccessSettingsActivity, "This folder have already been added !", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private suspend fun isFolderSAFExist(folderUriTree : FolderUriTree?): Boolean {
        if((mFolderUriTreeAdapter?.itemCount ?: 0) > 0 && folderUriTree != null){
            var returnFalse = 0
            for(i in ((mFolderUriTreeAdapter?.itemCount ?: 0) - 1) downTo  0){
                val tempData : FolderUriTree = mFolderUriTreeAdapter?.currentList?.get(i) ?: return false
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
                    if(tempData.normalizeScheme.toString() > folderUriTree.normalizeScheme.toString()){
                        removeFolderUriTree(mFolderUriTreeAdapter?.currentList?.get(i))
                    }else{
                        returnFalse++
                    }
                }
            }
            if(returnFalse > 0)
                return true
        }
        return false
    }
    private fun removeFolderUriTree(i: FolderUriTree?) {
        CoroutineScope(Dispatchers.IO).launch {
            mFolderUriTreeViewModel.deleteItem(i)
        }
    }
}