package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository): SearchHistoryInteractor {
    override fun get(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    override fun clear() {
        searchHistoryRepository.clearHistory()
    }

    override fun add(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }
}