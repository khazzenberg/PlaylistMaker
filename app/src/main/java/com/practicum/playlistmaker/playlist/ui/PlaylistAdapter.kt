package com.practicum.playlistmaker.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.domain.model.Playlist

class PlaylistAdapter(
    val playlistClickListener: PlaylistClickListener
): RecyclerView.Adapter<PlaylistViewHolder>(){
    var playlists = listOf<Playlist>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { playlistClickListener.onPlaylistClick(playlist.id) }
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlistId: Int)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}