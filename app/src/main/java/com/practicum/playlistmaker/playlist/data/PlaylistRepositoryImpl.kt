package com.practicum.playlistmaker.playlist.data

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.practicum.playlistmaker.playlist.domain.db.PlaylistRepository
import com.practicum.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity =  playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }
    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlist = appDatabase.playlistDao()
            .getAllPlaylists()
        emit(convertFromPlaylistEntityToPlaylist(playlist))
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist,track: Track) {
        playlist.tracks.add(track.trackId)
        playlist.count = playlist.tracks.size
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
        val trackInPlaylistEntity = playlistDbConvertor.mapTrackInPlaylist(track)
        appDatabase.trackInPlaylistDao().insertTrack(trackInPlaylistEntity)
    }

    fun convertFromPlaylistEntityToPlaylist(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }


}