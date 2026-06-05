package com.practicum.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(interactor: PlaylistInteractor, val playlist: Playlist) :
    CreatePlaylistViewModel(interactor) {
    private val statePlaylistInfoLiveData = MutableLiveData<PlaylistInfoState>()
    fun observePlaylistInfoState(): LiveData<PlaylistInfoState> = statePlaylistInfoLiveData

    init {
        fillData()
    }

    fun fillData() {
        processResult(playlist)
    }

    fun processResult(playlistLoc: Playlist) {
        if (playlistLoc.name.isEmpty())
            renderStatePlaylistInfo(PlaylistInfoState.Empty(""))
        else
            renderStatePlaylistInfo(PlaylistInfoState.Content(playlistLoc))
    }

    fun renderStatePlaylistInfo(state: PlaylistInfoState) {
        statePlaylistInfoLiveData.postValue(state)
    }

    override fun createPlaylist(name: String, description: String, image: String) {
        viewModelScope.launch {
            val editPlaylist = Playlist(
                id = playlist.id,
                name = name,
                description = description,
                image = image,
                tracks = playlist.tracks,
                count = playlist.count
            )
            interactor.updatePlaylist(editPlaylist)
        }
    }
}