package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.player.domain.models.Track

interface SearchHistoryInteractor {
    fun get(consumer: HistoryConsumer)
    fun clear()
    fun add(track: Track)

    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>?)
    }
}