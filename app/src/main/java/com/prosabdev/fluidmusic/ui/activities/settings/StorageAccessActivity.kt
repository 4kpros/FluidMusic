package com.prosabdev.fluidmusic.ui.activities.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.PlayerMoreDialog
import com.prosabdev.fluidmusic.ui.bottomsheetdialogs.StorageAccessDialog
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.utils.MediaFileScanner
import com.prosabdev.fluidmusic.viewmodels.StorageAccessActivityViewModel
import kotlinx.coroutines.launch

@BuildCompat.PrereleaseSdkCheck class StorageAccessActivity : AppCompatActivity() {

    private lateinit var mActivityStorageAccessBinding: ActivityStorageAccessBinding

    private val mStorageAccessActivityViewModel: StorageAccessActivityViewModel by viewModels()

    private var mEmptyBottomAdapter: EmptyBottomAdapter? = null
    private var mStorageAccessAdapter: StorageAccessAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    private var mFolderSelectionList : ArrayList<FolderSAF> = ArrayList<FolderSAF>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mActivityStorageAccessBinding = DataBindingUtil.setContentView(this, R.layout.activity_storage_access)

        initViews()
        setupAdapter()
        observeLiveData()
        checkInteractions()
        registerOnBackPressedCallback()
    }

    private fun registerOnBackPressedCallback() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if(mStorageAccessActivityViewModel.getHaveBeenUpdated().value == true){
                    showDialogToAlertSaveBefore()
                }else{
                    finish()
                }
            }
        } else {
            onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.i(ConstantValues.TAG, "On back pressed. Storage Access changed = ${mStorageAccessActivityViewModel.getHaveBeenUpdated().value}")
                    if(mStorageAccessActivityViewModel.getHaveBeenUpdated().value == true){
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
        mStorageAccessActivityViewModel.getRemoveFolderSAF().observe(this){
            if(it >= 0){
                mStorageAccessActivityViewModel.setRemoveFolderSAF(-1)
                removeFolder(it)
            }
        }
        mStorageAccessActivityViewModel.getAddFolderSAF().observe(this){
            addToFolderList(it)
        }
        mStorageAccessActivityViewModel.getFoldersList().observe(this){
            updateLoadingUI(it)
        }
        mStorageAccessActivityViewModel.getRequestAddFolder().observe(this){
            if(it > 0)
                requestForSAF()
        }
    }

    private fun requestForSAF() {
        mOpenSAFDocumentTreeLauncher.launch(null)
    }
    private fun updateLoadingUI(folderSAFS: ArrayList<FolderSAF>) {
        if(folderSAFS.size > 0){
            //Hide loading view
            if(mActivityStorageAccessBinding.constraintLoadingContent.alpha == 1.0f){
                CustomAnimators.crossFadeDown(mActivityStorageAccessBinding.constraintLoadingContent)
                CustomAnimators.crossFadeDown(mActivityStorageAccessBinding.textLoadingDetails)
            }
        }else{
            //show loading view
            if(mActivityStorageAccessBinding.constraintLoadingContent.visibility != View.VISIBLE){
                CustomAnimators.crossFadeUp(mActivityStorageAccessBinding.constraintLoadingContent)
                CustomAnimators.crossFadeUp(mActivityStorageAccessBinding.textLoadingDetails)
            }
        }
    }
    private fun addToFolderList(it: FolderSAF?) {
        if(it != null){
            mFolderSelectionList.add(it)
            mStorageAccessAdapter?.notifyItemInserted(mFolderSelectionList.size - 1)
            mStorageAccessActivityViewModel.setAddFolderSAF(null)
        }
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
        StorageAccessDialog().show(supportFragmentManager, StorageAccessDialog.TAG)
    }

    private fun saveFolderSAF() {
        //
    }

    private fun requestNewFolderFromSAF() {
        var tempReCounter = mStorageAccessActivityViewModel.getRequestAddFolder().value ?: 0
        tempReCounter++
        mStorageAccessActivityViewModel.setRequestAddFolder(tempReCounter)
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
        mActivityStorageAccessBinding.recyclerView.adapter = concatAdapter

        mLayoutManager = GridLayoutManager(this.baseContext, spanCount, GridLayoutManager.VERTICAL, false)
        mActivityStorageAccessBinding.recyclerView.layoutManager = mLayoutManager

    }
    private fun onShowRemoveFolderDialog(position: Int) {
        if (position >= (mStorageAccessActivityViewModel.getFoldersList().value?.size ?: 0))
            return
        MaterialAlertDialogBuilder(this)
            .setTitle("Remove this folder ${mStorageAccessActivityViewModel.getFoldersList().value?.get(position)?.path} ?")
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
        mStorageAccessActivityViewModel.removeFromFoldersList(position)
        mStorageAccessAdapter?.notifyItemRemoved(position)
        mStorageAccessAdapter?.notifyItemRangeChanged(position, mStorageAccessAdapter!!.itemCount)
    }

    private fun initViews() {
        CustomViewModifiers.updateTopViewInsets(mActivityStorageAccessBinding.coordinator)
        CustomViewModifiers.updateBottomViewInsets(mActivityStorageAccessBinding.constraintMainContainer)
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
    private fun formatAndAddFolderSource(uri: Uri) {
        val documentFile = DocumentFile.fromTreeUri(this, uri)

        val tempFolderSAF : FolderSAF = FolderSAF()
        tempFolderSAF.name = documentFile?.name
        tempFolderSAF.uriTree = documentFile?.uri
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
            mStorageAccessActivityViewModel.setAddFolderSAF(tempFolderSAF)
        }else{
            Toast.makeText(this, "This folder have already been added", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isFolderSAFExist(folderSAF : FolderSAF): Boolean {
        if(mStorageAccessActivityViewModel.getFoldersList().value != null &&
            (mStorageAccessActivityViewModel.getFoldersList().value?.size ?: 0) > 0
        ){
            val tempSize = mStorageAccessActivityViewModel.getFoldersList().value?.size ?: 0
            var returnFalse = 0
            for(i in 0 until tempSize){
                val tempData : FolderSAF? = mStorageAccessActivityViewModel.getFoldersList().value?.get(i)
                if(tempData != null){
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
                            mStorageAccessActivityViewModel.setRemoveFolderSAF(i)
                        }else{
                            returnFalse++
                        }
                    }
                }
            }
            if(returnFalse > 0)
                return true
        }
        return false
    }
}