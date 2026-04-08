package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Result<List<Track>> {
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))

            if (response.resultCode == 200) {
                return Result.success((response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })

            } else {
                Result.failure(IllegalArgumentException(""))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}