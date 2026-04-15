package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { MediaPlayer() }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { (track: Track) -> AudioPlayerViewModel(track, get()) }
    viewModel { SettingsViewModel(get(),get()) }
}