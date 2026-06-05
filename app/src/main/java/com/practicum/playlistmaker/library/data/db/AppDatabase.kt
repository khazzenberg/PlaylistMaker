package com.practicum.playlistmaker.library.data.db

import com.practicum.playlistmaker.favoritetracks.data.db.dao.TrackDao
import com.practicum.playlistmaker.favoritetracks.data.db.entity.TrackEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.player.data.db.dao.TrackInPlaylistDao
import com.practicum.playlistmaker.player.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.playlist.data.db.entity.PlaylistEntity


@Database(version = 3, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

}