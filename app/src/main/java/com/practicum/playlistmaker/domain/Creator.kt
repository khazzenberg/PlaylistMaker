package com.practicum.playlistmaker.domain

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.TrackRepositoryImpl
import com.practicum.playlistmaker.data.dto.SearchHistoryStorage
import com.practicum.playlistmaker.data.dto.SettingsStorage
import com.practicum.playlistmaker.data.network.RetrofitClient
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    const val PLAYLISTMAKER_PREFERENCES = "playlistmaker"
    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitClient())
    }

    private fun getSettingsRepository(sharedPreferences: SharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(SettingsStorage(sharedPreferences))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPreferences = context.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
        return SettingsInteractorImpl(getSettingsRepository(sharedPreferences))
    }

    private fun getSearchHistoryRepository(sharedPreferences: SharedPreferences): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SearchHistoryStorage(sharedPreferences))
    }

    fun provideSearchTracksInteractor(context: Context): SearchHistoryInteractor {
        val sharedPreferences = context.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(sharedPreferences))
    }
}