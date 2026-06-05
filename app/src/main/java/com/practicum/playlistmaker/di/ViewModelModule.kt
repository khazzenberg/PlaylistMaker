package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.playlist.presentation.CreatePlaylistViewModel
import com.practicum.playlistmaker.favoritetracks.presentation.FavoriteTracksViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.playlist.presentation.EditPlaylistViewModel
import com.practicum.playlistmaker.playlist.presentation.PlaylistCardViewModel
import com.practicum.playlistmaker.playlist.presentation.PlaylistViewModel
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { MediaPlayer() }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { (track: Track) -> AudioPlayerViewModel(track, get(), get(), get()) }
    viewModel { SettingsViewModel(get(),get()) }
    viewModel { FavoriteTracksViewModel(get()) }
    viewModel { PlaylistViewModel(get()) }
    viewModel { CreatePlaylistViewModel(get()) }
    viewModel { (playlistId: Int) -> PlaylistCardViewModel(playlistId, get()) }
    viewModel { (playlist: Playlist) -> EditPlaylistViewModel(get(), playlist) }
}