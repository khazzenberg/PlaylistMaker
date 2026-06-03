package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)
    @Query("SELECT * FROM tracks_table ORDER BY id DESC")
    suspend fun getAllTracks(): List<TrackEntity>
    @Query("DELETE FROM tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)
    @Query("SELECT trackId FROM tracks_table WHERE trackId = :trackId")
    suspend fun getTrackId(trackId: Int): Int
    @Transaction
    suspend fun deleteAndInsertTrack(track: TrackEntity) {
        deleteTrack(track.trackId)
        insertTrack(track)}
}