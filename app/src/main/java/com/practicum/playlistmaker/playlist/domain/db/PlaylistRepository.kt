package com.practicum.playlistmaker.playlist.domain.db

import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist,track: Track)
    fun getPlaylist(playlistId:Int): Flow<Playlist>
    fun getTracksInPlaylist(trackListId: List<Int>): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun sharePlaylist(playlist: Playlist, tracks: List<Track>)
    suspend fun updatePlaylist(playlist: Playlist)
}