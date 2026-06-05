package com.practicum.playlistmaker.playlist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.playlist.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class)
    suspend fun insertPlaylist(playlist: PlaylistEntity)
    @Query("SELECT * FROM playlists_table ORDER BY id DESC")
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    @Query("SELECT * FROM playlists_table WHERE id =:playlistId")
    fun getPlaylist(playlistId: Int): Flow<PlaylistEntity?>
    @Query("DELETE FROM playlists_table WHERE id =:playlistId")
    suspend fun deletePlaylist(playlistId: Int)
}