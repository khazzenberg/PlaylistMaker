package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun get(): List<Track>
    fun clear()
    fun add(track: Track)
}