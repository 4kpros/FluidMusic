package com.prosabdev.fluidmusic.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.prosabdev.fluidmusic.ui.fragments.explore.*

class TabLayoutAdapter(
    fragment : Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 8
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllSongsFragment.newInstance()
            1 -> AlbumsFragment.newInstance()
            2 -> ArtistsFragment.newInstance()
            3 -> FoldersFragment.newInstance()
            4 -> GenresFragment.newInstance()
            5 -> AlbumArtistsFragment.newInstance()
            6 -> ComposersFragment.newInstance()
            7 -> YearsFragment.newInstance()
            else -> AllSongsFragment.newInstance()
        }
    }
}