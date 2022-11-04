package com.prosabdev.fluidmusic.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.prosabdev.fluidmusic.ui.fragments.explore.*
import com.prosabdev.fluidmusic.utils.ConstantValues

class TabLayoutAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 5
    }
    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        when (position) {
            0 -> {
                fragment = FoldersFragment.newInstance()
            }
            1 -> {
                fragment = AllSongsFragment.newInstance(1)
            }
            2 -> {
                fragment = AlbumsFragment.newInstance()
            }
            3 -> {
                fragment = ArtistsFragment.newInstance()
            }
            4 -> {
                fragment = GenresFragment.newInstance()
            }
        }

        return fragment
    }
}