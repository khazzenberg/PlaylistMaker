package com.practicum.playlistmaker.playlist.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.models.Track

class TrackInPlaylistAdapter(
    val clickListener: TrackClickListener,
    val longClickListener: TrackClickListener
) :
    RecyclerView.Adapter<TrackInPlaylistViewHolder>() {
    var tracks = listOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackInPlaylistViewHolder =
        TrackInPlaylistViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: TrackInPlaylistViewHolder,
        position: Int
    ) {
        holder.bind(tracks.get(position))
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks.get(position)) }
        holder.itemView.setOnLongClickListener { longClickListener.onTrackClick(tracks.get(position))
            true }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}