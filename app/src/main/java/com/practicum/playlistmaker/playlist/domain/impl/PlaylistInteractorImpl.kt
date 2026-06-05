package com.practicum.playlistmaker.playlist.domain.impl

import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.db.PlaylistRepository
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(val playlistRepository: PlaylistRepository): PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun insertTrackInPlaylist(playlist: Playlist,track: Track) {
        playlistRepository.addTrackToPlaylist(playlist,track)
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylist(playlistId)
    }

    override fun getTrackInPlaylist(trackListId: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTracksInPlaylist(trackListId)
    }

    override suspend fun deleteTrackFromPlaylist(
        playlist: Playlist,
        track: Track
    ) {
        playlistRepository.deleteTrackFromPlaylist(playlist, track)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun sharePlaylist(
        playlist: Playlist,
        tracks: List<Track>
    ) {
        playlistRepository.sharePlaylist(playlist, tracks)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }
}