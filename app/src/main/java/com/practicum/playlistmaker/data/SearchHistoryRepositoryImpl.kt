package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.SearchHistoryStorage
import com.practicum.playlistmaker.data.dto.TrackHistoryDto
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import kotlin.text.toLong

class SearchHistoryRepositoryImpl(private val searchHistoryStorage: SearchHistoryStorage) :
    SearchHistoryRepository {
    override fun getHistory(): List<Track> {
        return searchHistoryStorage.getHistory().map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis.toLong(),
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

    override fun clearHistory() {
        searchHistoryStorage.clearHistory()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryStorage.addTrackToHistory(
            TrackHistoryDto(
                track.trackId,
                track.trackName,
                track.artistName,
                track.trackTimeMillis.toString(),
                track.artworkUrl100,
                track.collectionName.toString(),
                track.releaseDate.toString(),
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )
        )
    }
}