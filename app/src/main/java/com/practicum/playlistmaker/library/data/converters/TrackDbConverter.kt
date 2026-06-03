package com.practicum.playlistmaker.library.data.converters

import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            id = 0,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl?: "",
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl
        )
    }
}