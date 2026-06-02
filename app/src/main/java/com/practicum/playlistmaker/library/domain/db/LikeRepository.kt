package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface LikeRepository {
    fun likeTracks(): Flow<List<Track>>
    suspend fun unlikeTrack(track: Track)
    suspend fun likeTrack(track: Track)
    suspend fun getTrackId(track: Track): Int
}