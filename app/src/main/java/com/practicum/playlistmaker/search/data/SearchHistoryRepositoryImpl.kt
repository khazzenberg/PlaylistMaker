package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.creator.Result
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(private val storage: StorageClient<ArrayList<Track>>) :
    SearchHistoryRepository {
    override fun getHistory(): Result<List<Track>> {
        val tracks = storage.getData() ?: listOf()
        return Result.Success(tracks)
    }

    override fun clearHistory() {
        storage.clearData()
    }

    override fun addTrackToHistory(track: Track) {
        val tracks = storage.getData() ?: arrayListOf()
        tracks.removeAll { track.trackId == it.trackId }
        if (tracks.size >= MAX_SIZE) {
            tracks.removeAt(tracks.lastIndex)
        }
        tracks.add(0, track)
        storage.storeData(tracks)
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}