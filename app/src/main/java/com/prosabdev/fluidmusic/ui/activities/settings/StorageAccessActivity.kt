package com.prosabdev.fluidmusic.ui.activities.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.EmptyBottomAdapter
import com.prosabdev.fluidmusic.adapters.StorageAccessAdapter
import com.prosabdev.fluidmusic.databinding.ActivityStorageAccessBinding
import com.prosabdev.fluidmusic.models.FolderSAF
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.StorageAccessDialog
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.utils.SharedPreferenceManager
import com.prosabdev.fluidmusic.viewmodels.StorageAccessActivityViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class StorageAccessActivity : AppCompatActivity() {

    private lateinit var mActivityStorageAccessBinding: ActivityStorageAccessBinding

    private val mStorageAccessActivityViewModel: StorageAccessActivityViewModel by viewModels()

    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mStorageAccessAdapter: StorageAccessAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mHaveBeenUpdated: Boolean = false

    private var mFolderSelectionList : ArrayList<FolderSAF> = ArrayList<FolderSAF>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityStorageAccessBinding = DataBindingUtil.setContentView(this, R.layout.activity_storage_access)

        initViews()
        MainScope().launch {
            loadFolderSAF()
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
                if(mHaveBeenUpdated){
                    showDialogToAlertSaveBefore()
                }else{
                    finish()
                }
            }
        } else {
            onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(mHaveBeenUpdated){
                        showDialogToAlertSaveBefore()
                    }else{
                        finish()
                    }
                }
            })
        }
    }
    private fun showDialogToAlertSaveBefore() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Discard changes ?")
            .setMessage("You have make changes on storage access. Do you want to quit anyway ?")
            .setNegativeButton("Quit") { dialog, which ->
                finish()
            }
            .setPositiveButton("Stay") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun observeLiveData() {
        mStorageAccessActivityViewModel.getFoldersCounter().observe(this){
            updateLoadingUI(it)
        }
        mStorageAccessActivityViewModel.getRemoveAllFoldersCounter().observe(this){
            MainScope().launch {
                removeAllFolders(it)
            }
        }
    }

    private suspend fun removeAllFolders(i: Int) = coroutineScope{
        if(i <= 0 || mFolderSelectionList.size <= 0)
            return@coroutineScope
        val folderSize: Int = mFolderSelectionList.size
        mStorageAccessAdapter?.notifyItemRangeRemoved(0, folderSize)
        mFolderSelectionList.clear()
        mStorageAccessActivityViewModel.setFoldersCounter(mFolderSelectionList.size)
        mHaveBeenUpdated = true
    }

    private fun updateLoadingUI(folderSAFS: Int) {
        mActivityStorageAccessBinding.foldersCounter = folderSAFS
    }

    private fun checkInteractions() {
        mActivityStorageAccessBinding.buttonAddFolder.setOnClickListener{
            requestNewFolderFromSAF()
        }
        mActivityStorageAccessBinding.buttonNext.setOnClickListener{
            saveFolderSAF()
        }
        mActivityStorageAccessBinding.topAppBar.setNavigationOnClickListener(){
            onBackPressedDispatcher.onBackPressed()
        }
        mActivityStorageAccessBinding.topAppBar.setOnMenuItemClickListener {
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
        mStorageAccessAdapter = StorageAccessAdapter(mFolderSelectionList, object : StorageAccessAdapter.OnItemClickListener{
            override fun onRemoveFolder(position: Int) {
                onShowRemoveFolderDialog(position)
            }
        })
        val emptyList: ArrayList<String> = ArrayList()
        emptyList.add("")
        mEmptyBottomAdapter = EmptyBottomAdapter(emptyList)

        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(mStorageAccessAdapter!!)
        concatAdapter.addAdapter(mEmptyBottomAdapter!!)
        mActivityStorageAccessBinding.recyclerView.adapter = concatAdapter

        mLayoutManager = GridLayoutManager(this.baseContext, spanCount, GridLayoutManager.VERTICAL, false)
        mActivityStorageAccessBinding.recyclerView.layoutManager = mLayoutManager

    }
    private fun onShowRemoveFolderDialog(position: Int) {
        if (position >= mFolderSelectionList.size)
            return
        MaterialAlertDialogBuilder(this)
            .setTitle("Remove this folder ${mFolderSelectionList[position].path} ?")
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
        if (position < 0)
            return
        mFolderSelectionList.removeAt(position)
        mStorageAccessAdapter?.notifyItemRemoved(position)
        mStorageAccessAdapter?.notifyItemRangeChanged(position, mStorageAccessAdapter!!.itemCount)
        mStorageAccessActivityViewModel.setFoldersCounter(mFolderSelectionList.size)
        mHaveBeenUpdated = true
    }


    private fun saveFolderSAF() {
        SharedPreferenceManager.saveSelectionFolderFromSAF(this.baseContext, mFolderSelectionList as List<FolderSAF>)
        mHaveBeenUpdated = false
//        startActivity(Intent(this, MediaScannerActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
        Toast.makeText(this, "Folder selection saved !", Toast.LENGTH_SHORT).show()
    }

    private suspend fun loadFolderSAF() = coroutineScope {
        val mTempLoadedFiles : List<FolderSAF>? = SharedPreferenceManager.loadSelectionFolderFromSAF(this@StorageAccessActivity)
        if(mTempLoadedFiles != null){
            mFolderSelectionList = mTempLoadedFiles as ArrayList<FolderSAF>
            mStorageAccessActivityViewModel.setFoldersCounter(mFolderSelectionList.size)
        }
    }

    private fun initViews() {
        CustomViewModifiers.updateTopViewInsets(mActivityStorageAccessBinding.coordinator)
        CustomViewModifiers.updateBottomViewInsets(mActivityStorageAccessBinding.constraintMainContainer)
        mActivityStorageAccessBinding.foldersCounter = mStorageAccessActivityViewModel.getFoldersCounter().value
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
            lifecycleScope.launch {
                formatAndAddFolderSource(uri)
            }
        }
    }
    private suspend fun formatAndAddFolderSource(uri: Uri) = coroutineScope{
        val documentFile = DocumentFile.fromTreeUri(this@StorageAccessActivity, uri)

        val tempFolderSAF : FolderSAF = FolderSAF()
        tempFolderSAF.name = documentFile?.name
        tempFolderSAF.uriTree = documentFile?.uri.toString()
        tempFolderSAF.lastPathSegment = uri.lastPathSegment
        tempFolderSAF.pathTree = uri.path.toString().trim()
        tempFolderSAF.normalizeScheme = uri.normalizeScheme().toString()
        tempFolderSAF.path = (uri.lastPathSegment ?: "").substringAfter(":").trim()
        if(tempFolderSAF.path!!.isEmpty())
            tempFolderSAF.path = documentFile?.name.toString().trim()
        tempFolderSAF.deviceName =
            if((uri.lastPathSegment ?: "").substringBefore(":") == "primary")
                MediaFileScanner.getDeviceName()
            else
                MediaFileScanner.getDeviceName()

        if(!isFolderSAFExist(tempFolderSAF)){
            addToFolderList(tempFolderSAF)
        }else{
            Toast.makeText(this@StorageAccessActivity, "This folder have already been added !", Toast.LENGTH_SHORT).show()
        }
    }
    private suspend fun addToFolderList(it: FolderSAF?) {
        if(it != null){
            mFolderSelectionList.add(it)
            mStorageAccessAdapter?.notifyItemInserted(mFolderSelectionList.size - 1)
            mStorageAccessActivityViewModel.setFoldersCounter(mFolderSelectionList.size)
            mHaveBeenUpdated = true
        }
    }
    private suspend fun isFolderSAFExist(folderSAF : FolderSAF): Boolean = coroutineScope {
        if(mFolderSelectionList.size > 0){
            var returnFalse = 0
            for(i in (mFolderSelectionList.size-1) downTo  0){
                val tempData : FolderSAF = mFolderSelectionList[i]
                if(
                    tempData.normalizeScheme.toString() == folderSAF.normalizeScheme.toString() ||
                    (
                            (
                                    tempData.normalizeScheme.toString().contains(folderSAF.normalizeScheme.toString()) ||
                                            folderSAF.normalizeScheme.toString().contains(tempData.normalizeScheme.toString())
                                    )
                                    &&
                                    tempData.pathTree.toString() != folderSAF.pathTree.toString()
                            )
                ) {
                    if(tempData.normalizeScheme.toString() > folderSAF.normalizeScheme.toString()){
                        removeFromFolderList(i)
                    }else{
                        returnFalse++
                    }
                }
            }
            if(returnFalse > 0)
                return@coroutineScope true
        }
        return@coroutineScope false
    }
    private suspend fun removeFromFolderList(i: Int) {
        mFolderSelectionList.removeAt(i)
        mStorageAccessAdapter?.notifyItemRemoved(i)
        mStorageAccessActivityViewModel.setFoldersCounter(mFolderSelectionList.size)
        mHaveBeenUpdated = true
    }
}