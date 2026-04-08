package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.SearchHistoryStorage
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(private val searchHistoryStorage: SearchHistoryStorage) :
    SearchHistoryRepository {
    override fun getHistory(): List<Track> {
        return searchHistoryStorage.getHistory()
    }

    override fun clearHistory() {
        searchHistoryStorage.clearHistory()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryStorage.addTrackToHistory(track)
    }
}