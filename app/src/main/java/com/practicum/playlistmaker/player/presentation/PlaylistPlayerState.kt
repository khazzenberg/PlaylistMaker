package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.playlist.domain.model.Playlist

sealed interface PlaylistPlayerState {
    data class Content(val playlists: List<Playlist>) : PlaylistPlayerState
    data class Empty(val isEmpty: String) : PlaylistPlayerState
}