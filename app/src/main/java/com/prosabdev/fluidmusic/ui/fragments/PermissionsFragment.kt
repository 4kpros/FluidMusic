package com.prosabdev.fluidmusic.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT_TREE
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.FragmentPermissionsBinding
import com.prosabdev.fluidmusic.utils.ConstantValues
import com.prosabdev.fluidmusic.utils.CustomAnimators
import com.prosabdev.fluidmusic.utils.CustomViewModifiers
import com.prosabdev.fluidmusic.utils.PermissionsManager
import com.prosabdev.fluidmusic.viewmodels.PermissionsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PermissionsFragment : Fragment() {

    private lateinit var mFragmentPermissionsBinding: FragmentPermissionsBinding

    private val mPermissionsFragmentViewModel: PermissionsFragmentViewModel by activityViewModels()

    private lateinit var mContext: Context
    private lateinit var mActivity: FragmentActivity

    private var treeUri: String?
        get() = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("tree_uri", null)
        set(value) {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                putString("tree_uri", value)
            }
        }

    private val mOpenSAFDocumentTreeLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            treeUri = uri.toString()
            val takeFlags: Int =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
            lifecycleScope.launch {

            }
        }
    }


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
        mFragmentPermissionsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_permissions,container,false)
        val view = mFragmentPermissionsBinding.root

        initViews()
        checkInteractions()
        observeLiveData()

        return view
    }

    private fun observeLiveData() {
        mPermissionsFragmentViewModel.getHaveStorageAccess().observe(mActivity
        ) {
            if (it == true)
                preloadInitialData()
        }
    }

    private fun preloadInitialData() {
        hidePermissionsUI()
        openSomeActivityForResult()
    }
    fun openSomeActivityForResult() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        }
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
//            doSomeOperations()
        }
    }
    fun openDirectory(pickerInitialUri: Uri) {
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 12)
    }

    private fun hidePermissionsUI() {
        mFragmentPermissionsBinding.haveStoragePermissions = true
        CustomAnimators.crossFadeUp(mFragmentPermissionsBinding.hoverGetStartedButton, true)
    }

    private fun checkInteractions() {
        mFragmentPermissionsBinding.buttonGetStarted.setOnClickListener{
            requestStorageAccessPermissions()
        }
    }

    private fun requestStorageAccessPermissions() {
        PermissionsManager.requestStoragePermission(mActivity)
    }

    private fun initViews() {
        CustomAnimators.crossFadeDown(mFragmentPermissionsBinding.hoverGetStartedButton, false)

        CustomViewModifiers.updateTopViewInsets(mFragmentPermissionsBinding.linearWelcomeContainer)
        CustomViewModifiers.updateBottomViewInsets(mFragmentPermissionsBinding.linearButtonGetStartedContainer)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PermissionsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}