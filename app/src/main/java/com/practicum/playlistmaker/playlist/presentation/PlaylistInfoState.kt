package com.practicum.playlistmaker.playlist.presentation

import com.practicum.playlistmaker.playlist.domain.model.Playlist

sealed interface PlaylistInfoState {
    data class Content(val playlist: Playlist) : PlaylistInfoState
    data class Empty(val isEmpty: String) : PlaylistInfoState
}