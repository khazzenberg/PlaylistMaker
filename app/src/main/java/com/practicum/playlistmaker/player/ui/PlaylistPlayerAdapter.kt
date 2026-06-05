package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.domain.model.Playlist

class PlaylistPlayerAdapter(val clickListener: PlaylistClickListener): RecyclerView.Adapter<PlaylistPlayerViewHolder>(){
    var playlists = listOf<Playlist>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistPlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_small_view, parent, false)
        return PlaylistPlayerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistPlayerViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}
