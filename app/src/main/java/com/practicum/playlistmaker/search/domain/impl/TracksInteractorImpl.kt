package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(
        expression: String
    ): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Result.Success -> {
                    Pair(result.data, null)
                }

                is Result.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}