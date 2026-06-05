package com.practicum.playlistmaker.playlist.presentation

import com.practicum.playlistmaker.playlist.domain.model.Playlist

sealed interface PlaylistState {
    data class Content(val playlists: List<Playlist>) : PlaylistState
    data class Empty(val isEmpty: String) : PlaylistState
}