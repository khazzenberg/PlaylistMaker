package com.practicum.playlistmaker.favoritetracks.domain.impl

import com.practicum.playlistmaker.favoritetracks.domain.db.LikeInteractor
import com.practicum.playlistmaker.favoritetracks.domain.db.LikeRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class LikeInteractorImpl(private val likeRepository: LikeRepository): LikeInteractor {
    override fun likeTracks(): Flow<List<Track>> {
        return likeRepository.likeTracks()
    }

    override suspend fun unlikeTrack(track: Track) {
        likeRepository.unlikeTrack(track)
    }

    override suspend fun likeTrack(track: Track) {
        likeRepository.likeTrack(track)
    }

    override suspend fun getTrackId(track: Track) : Int {
        return likeRepository.getTrackId(track)
    }
}