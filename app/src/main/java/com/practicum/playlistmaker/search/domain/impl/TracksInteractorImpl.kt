package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        executor.execute {
            when (val result = repository.searchTracks(expression)) {
                is Result.Success -> {
                    consumer.consume(result.data, null)
                }

                is Result.Error -> {
                    consumer.consume(null, result.message)
                }
            }
        }
    }
}