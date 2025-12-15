package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter (
    private val tracks: MutableList<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    var onTrackClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(tracks.get(position))
        holder.itemView.setOnClickListener {  }
        holder.itemView.setOnClickListener {
            onTrackClick?.invoke(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
