package com.prosabdev.fluidmusic.ui.fragments.explore

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.adapters.AlbumItemAdapter
import com.prosabdev.fluidmusic.adapters.SongItemAdapter
import com.prosabdev.fluidmusic.models.AlbumItem
import com.prosabdev.fluidmusic.models.SongItem

class AlbumsFragment : Fragment() {

    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null

    private var mAlbumItemAdapter: AlbumItemAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    private var mAlbumList : ArrayList<AlbumItem> = ArrayList<AlbumItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AlbumsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}