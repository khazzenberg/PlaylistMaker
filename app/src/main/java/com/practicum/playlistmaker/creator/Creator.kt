package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.settings.data.storage.SettingsStorage
import com.practicum.playlistmaker.search.data.network.RetrofitClient
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.player.domain.api.TracksInteractor
import com.practicum.playlistmaker.player.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.player.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.search.data.storage.PrefsStorageClient
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TrackRepositoryImpl(RetrofitClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getSettingsRepository(sharedPreferences: SharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(SettingsStorage(sharedPreferences))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPreferences = context.getSharedPreferences(PM_PREFERENCES, Context.MODE_PRIVATE)
        return SettingsInteractorImpl(getSettingsRepository(sharedPreferences))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(provideSharingRepository(context, provideExternalNavigator(context)))
    }

    fun provideSharingRepository(context: Context, externalNavigator: ExternalNavigator): SharingRepositoryImpl{
        return SharingRepositoryImpl(context, externalNavigator)
    }

    fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigator(context)
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
                context,
                HISTORY_KEY,
                object : TypeToken<ArrayList<Track>>() {}.type
            )
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    const val PM_PREFERENCES = "playlistmaker"
    const val HISTORY_KEY = "HISTORY"
}