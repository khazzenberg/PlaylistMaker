package com.practicum.playlistmaker.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.domain.model.Playlist

class PlaylistCardAdapter: RecyclerView.Adapter<PlaylistCardViewHolder>(){
    var playlists = listOf<Playlist>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_small_view, parent, false)
        return PlaylistCardViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistCardViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}