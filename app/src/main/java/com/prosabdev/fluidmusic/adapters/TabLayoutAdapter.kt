package com.prosabdev.fluidmusic.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.prosabdev.fluidmusic.ui.fragments.explore.*

class TabLayoutAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 8
    }
    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        when (position) {
            0->{
                fragment = AllSongsFragment.newInstance(position)
            }
            1->{
                fragment = AlbumsFragment.newInstance(position)
            }
            2->{
                fragment = ArtistsFragment.newInstance(position)
            }
            3->{
                fragment = FoldersFragment.newInstance(position)
            }
            4->{
                fragment = GenresFragment.newInstance(position)
            }
            5->{
                fragment = AlbumArtistsFragment.newInstance(position)
            }
            6->{
                fragment = ComposersFragment.newInstance(position)
            }
            7->{
                fragment = YearsFragment.newInstance(position)
            }
        }

        return fragment
    }
}