package com.practicum.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.presentation.NameState
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(val interactor: PlaylistInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<NameState>()
    fun observeState(): LiveData<NameState> = stateLiveData

    fun changeName(name: String) {
        if (name.isEmpty()) {
            stateLiveData.postValue(NameState(isEmpty = true))

        } else {
            stateLiveData.postValue(NameState(isEmpty = false))
        }
    }

    open fun createPlaylist(name: String, description: String, image: String) {
        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = description,
                image = image,
                tracks = mutableListOf(),
                count = 0
            )
            interactor.insertPlaylist(playlist)
        }
    }

}