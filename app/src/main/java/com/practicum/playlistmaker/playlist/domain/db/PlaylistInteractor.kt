package com.practicum.playlistmaker.playlist.domain.db

import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor{
    suspend fun insertPlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun insertTrackInPlaylist(playlist: Playlist,track: Track)
}