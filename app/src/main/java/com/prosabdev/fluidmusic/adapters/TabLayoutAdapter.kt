package com.prosabdev.fluidmusic.adapters

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.prosabdev.fluidmusic.ui.fragments.explore.*

class TabLayoutAdapter(
    fragmentManager : FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 8
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        when (position) {
            0->{
                fragment = AllSongsFragment.newInstance()
            }
            1->{
                fragment = AlbumsFragment.newInstance()
            }
            2->{
                fragment = ArtistsFragment.newInstance()
            }
            3->{
                fragment = FoldersFragment.newInstance()
            }
            4->{
                fragment = GenresFragment.newInstance()
            }
            5->{
                fragment = AlbumArtistsFragment.newInstance()
            }
            6->{
                fragment = ComposersFragment.newInstance()
            }
            7->{
                fragment = YearsFragment.newInstance()
            }
        }
        return fragment
    }
}