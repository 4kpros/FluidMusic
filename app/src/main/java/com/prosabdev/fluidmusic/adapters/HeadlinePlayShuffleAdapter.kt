package com.prosabdev.fluidmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.prosabdev.fluidmusic.R

class HeadlinePlayShuffleAdapter(
    private val mHeadLines: ArrayList<Long>,
    private val mListener: OnItemClickListener
    ) : RecyclerView.Adapter<HeadlinePlayShuffleAdapter.HeadlinePlayShuffleHolder>() {

    interface OnItemClickListener {
        fun onPlayButtonClicked()
        fun onShuffleButtonClicked()
        fun onFilterButtonClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinePlayShuffleHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_play_shuffle, parent, false)

        return HeadlinePlayShuffleHolder(view)
    }

    override fun onBindViewHolder(holder: HeadlinePlayShuffleHolder, position: Int) {
        //Bind listener for to capture click events
        holder.bindListener(mListener)
    }

    override fun getItemCount(): Int {
        return mHeadLines.size
    }

    class HeadlinePlayShuffleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mPlayButton: MaterialButton? =
            itemView.findViewById<MaterialButton>(R.id.button_play)
        private var mShuffleButton: MaterialButton? =
            itemView.findViewById<MaterialButton>(R.id.button_shuffle)
        private var mFilterButton: MaterialButton? =
            itemView.findViewById<MaterialButton>(R.id.button_filter)

        //Method used to bind one listener with items events click
        fun bindListener(listener: OnItemClickListener) {
            mPlayButton?.setOnClickListener {
                listener.onPlayButtonClicked()
            }
            mShuffleButton?.setOnClickListener {
                listener.onShuffleButtonClicked()
            }
            mFilterButton?.setOnClickListener {
                listener.onFilterButtonClicked()
            }
        }
    }
}