package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.domain.db.LikeRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LikeRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : LikeRepository {
    override fun likeTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao()
            .getAllTracks()
        emit(convertFromTrackEntityToTrack(tracks))
    }

    override suspend fun unlikeTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity.trackId)
    }

    override suspend fun likeTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun getTrackId(track: Track): Int {
        val trackEntity = trackDbConvertor.map(track)
        return appDatabase.trackDao().getTrackId(trackEntity.trackId)
    }

    private fun convertFromTrackEntityToTrack(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}