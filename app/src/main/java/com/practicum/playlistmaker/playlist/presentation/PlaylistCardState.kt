package com.practicum.playlistmaker.playlist.presentation

import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed interface PlaylistCardState {
    data class Content(val playlist: Playlist, val tracks: List<Track>) : PlaylistCardState
    data class Empty(val isEmpty: String) : PlaylistCardState
}