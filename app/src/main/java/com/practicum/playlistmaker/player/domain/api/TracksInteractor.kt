package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage:String?)
    }
}