package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prosabdev.fluidmusic.R

class EmptyBottomAdapter(
    private val mEmptyList: ArrayList<String>
) : RecyclerView.Adapter<EmptyBottomAdapter.EmptyBottomHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyBottomHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_empty_bottom_space, parent, false)

        return EmptyBottomHolder(view)
    }

    override fun onBindViewHolder(holder: EmptyBottomHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return mEmptyList.size
    }


    class EmptyBottomHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}