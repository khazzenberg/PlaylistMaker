package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Result<List<Track>>> = flow  {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Result.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                with(response as TrackSearchResponse) {
                    val data = response.results.map{
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
                    }
                    emit(Result.Success(data))
                }
            }
            else -> {
                emit(Result.Error("Ошибка сервера"))
            }
        }
    }
}