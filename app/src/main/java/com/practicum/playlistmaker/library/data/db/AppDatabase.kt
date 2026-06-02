package com.practicum.playlistmaker.library.data.db

import com.practicum.playlistmaker.library.data.db.dao.TrackDao
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

}