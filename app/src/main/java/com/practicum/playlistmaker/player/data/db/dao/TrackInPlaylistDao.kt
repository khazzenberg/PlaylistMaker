package com.practicum.playlistmaker.player.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.player.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {
    @Query("SELECT * FROM tracks_in_playlist_table")
    fun getAllTracks(): Flow<List<TrackInPlaylistEntity>>
    @Query("DELETE FROM tracks_in_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)
    @Insert(entity = TrackInPlaylistEntity::class,onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertTrack(track: TrackInPlaylistEntity)
    @Transaction
    suspend fun deleteAndInsertTrack(track: TrackInPlaylistEntity) {
        deleteTrack(track.trackId)
        insertTrack(track)}
}