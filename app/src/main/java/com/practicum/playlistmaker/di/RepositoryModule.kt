package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<TracksRepository>{
        TrackRepositoryImpl(get())
    }
    factory<SearchHistoryRepository>{
        SearchHistoryRepositoryImpl(get())
    }
    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    factory<SharingRepository>{
        SharingRepositoryImpl(get(),get())
    }
}