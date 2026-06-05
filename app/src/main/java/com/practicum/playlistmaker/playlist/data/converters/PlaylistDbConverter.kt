package com.practicum.playlistmaker.playlist.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.player.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistDbConvertor {
    val gson = Gson()
    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id =playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            count = playlistEntity.count,
            tracks = gson.fromJson(playlistEntity.trackList, object : TypeToken<ArrayList<Int>>(){}.type),
            image = playlistEntity.image
        )
    }
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            image = playlist.image,
            trackList = gson.toJson(playlist.tracks, object : TypeToken<ArrayList<Int>>(){}.type),
            count = playlist.count
        )
    }

    fun mapTrackInPlaylist(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            id = 0,//??
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl ?: "",
        )
    }

    fun mapTrackInPlaylist(trackInPlaylistEntity: TrackInPlaylistEntity): Track {
        return Track(
            trackInPlaylistEntity.trackId,
            trackInPlaylistEntity.trackName,
            trackInPlaylistEntity.artistName,
            trackInPlaylistEntity.trackTimeMillis,
            trackInPlaylistEntity.artworkUrl100,
            trackInPlaylistEntity.collectionName,
            trackInPlaylistEntity.releaseDate,
            trackInPlaylistEntity.primaryGenreName,
            trackInPlaylistEntity.country,
            trackInPlaylistEntity.previewUrl
        )
    }
}