package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.player.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): Result<List<Track>>
}