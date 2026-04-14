package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.player.domain.api.TracksRepository
import com.practicum.playlistmaker.player.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Result<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Result.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Result.Success((response as TrackSearchResponse).results.map {
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
            }
            else -> {
                Result.Error("Ошибка сервера")
            }
        }
    }
}