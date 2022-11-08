package com.prosabdev.fluidmusic

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.google.android.material.color.DynamicColors
import com.google.android.material.navigation.NavigationView
import com.prosabdev.fluidmusic.ui.fragments.MainFragment
import com.prosabdev.fluidmusic.viewmodels.MainExploreFragmentViewModel

class MainActivity : AppCompatActivity(){

    private var mNavigationView : NavigationView? = null
    private var mDrawerLayout : DrawerLayout? = null
    private val mMainExploreFragmentViewModel: MainExploreFragmentViewModel by viewModels()

//    private val token: PlayingService.ServiceToken? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        token = PlayingService.bindToService(this, this)
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