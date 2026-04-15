package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): Result<List<Track>>
}