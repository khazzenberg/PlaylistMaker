package com.practicum.playlistmaker.playlist.data

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.player.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.practicum.playlistmaker.playlist.domain.db.PlaylistRepository
import com.practicum.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlist.data.storage.ExternalNavigatorPlaylist
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val externalNavigator: ExternalNavigatorPlaylist
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
        appDatabase.trackInPlaylistDao().deleteAndInsertTrack(trackInPlaylistEntity)
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist> {
        return appDatabase.playlistDao().getPlaylist(playlistId).map { entity ->
            if (entity != null) {
                playlistDbConvertor.map(entity)
            } else {
                Playlist(
                    id = -1,
                    name = "",
                    description = "",
                    image = "",
                    tracks = mutableListOf(),
                    count = 0
                )
            }
        }
    }

    override fun getTracksInPlaylist(trackListId: List<Int>): Flow<List<Track>> {
        return appDatabase.trackInPlaylistDao().getAllTracks().map { trackEntities ->
            val filteredTracks = trackEntities.filter { it.trackId in trackListId }
            val sortedTracks = filteredTracks.sortedBy { trackListId.indexOf(it.trackId) }
            convertFromTrackInPlaylistEntityToTrack(sortedTracks)
        }
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        playlist.tracks.remove(track.trackId)
        playlist.count = playlist.tracks.size
        val playlistEntity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
        isTrackInPlaylist(playlistDbConvertor.mapTrackInPlaylist(track).trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracksToDelete = playlist.tracks
        appDatabase.playlistDao().deletePlaylist(playlistDbConvertor.map(playlist).id)
        tracksToDelete.forEach { isTrackInPlaylist(it) }
    }

    override suspend fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {

        externalNavigator.sharePlaylist(getSharePlaylist(playlist, tracks))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(playlist))
    }

    private suspend fun isTrackInPlaylist(trackId: Int) {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        val isTrackInPlaylist = playlists.any { playlistEntity ->
            val playlist = playlistDbConvertor.map(playlistEntity)
            playlist.tracks.contains(trackId)
        }
        if (!isTrackInPlaylist)
            appDatabase.trackInPlaylistDao().deleteTrack(trackId)
    }

    fun convertFromPlaylistEntityToPlaylist(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    fun convertFromTrackInPlaylistEntityToTrack(tracks: List<TrackInPlaylistEntity>): List<Track> {
        return tracks.map { track -> playlistDbConvertor.mapTrackInPlaylist(track) }
    }

    fun getSharePlaylist(playlist: Playlist, tracks: List<Track>): String {
        val traсksDescr =
            when (playlist.count) {
                1 -> "трек"
                in 2..4 -> "трека"
                in 5..9, 0 -> "треков"
                else -> ""
            }
        // Формируем заголовок
        val playlistInfo = buildString {
            appendLine(playlist.name)
            if (playlist.description.isNotEmpty()) appendLine(playlist.description)
            appendLine("${playlist.count} $traсksDescr")
        }

        // Формируем список треков
        val tracksInfo = tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            })"
        }.joinToString("\n")

        return playlistInfo + tracksInfo
    }

}