package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.player.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): Result<List<Track>>
    fun clearHistory()
    fun addTrackToHistory(track: Track)
}