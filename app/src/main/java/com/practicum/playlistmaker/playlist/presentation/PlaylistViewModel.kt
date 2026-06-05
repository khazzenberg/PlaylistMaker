package com.practicum.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(val interactor: PlaylistInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            interactor.getAllPlaylists().collect { playlists -> processResult(playlists) }
        }
    }

    fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Empty(""))
        } else {
            renderState(PlaylistState.Content(playlists))
        }
    }

    fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }
}