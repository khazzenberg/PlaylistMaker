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
}