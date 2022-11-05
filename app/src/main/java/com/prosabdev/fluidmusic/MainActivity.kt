package com.prosabdev.fluidmusic

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.color.DynamicColors
import com.google.android.material.navigation.NavigationView
import com.prosabdev.fluidmusic.ui.fragments.MainExploreFragment
import com.prosabdev.fluidmusic.ui.fragments.MainFragment
import com.prosabdev.fluidmusic.ui.fragments.explore.AllSongsFragment
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel

class MainActivity : AppCompatActivity(){

    private var mNavigationView : NavigationView? = null
    private var mDrawerLayout : DrawerLayout? = null
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setup UI
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        setContentView(R.layout.activity_main)

        initViews(savedInstanceState)
        checkInteractions()
    }

    private fun checkInteractions() {
        mMainExploreFragmentViewModel.mActionBarState.observe(this, Observer { item ->
            if(item){
                mDrawerLayout?.open()
            }
        })
    }

    //Initialize views
    private fun initViews(savedInstanceState : Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.main_activity_fragment_container)
            }
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.navigation_view)

        mNavigationView?.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            mDrawerLayout?.close()
            true
        }
    }
}