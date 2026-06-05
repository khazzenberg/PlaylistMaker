package com.practicum.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistCardViewModel(
    private val playlistId: Int,
    private val interactor: PlaylistInteractor
) : ViewModel() {
    private val playlistCardStateLiveData = MutableLiveData<PlaylistCardState>()
    fun observePlaylistCardState(): LiveData<PlaylistCardState> = playlistCardStateLiveData
    private var playlistG: Playlist? = null
    private var tracksG: List<Track> = mutableListOf()

    init {
        fillCard()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fillCard() {
        viewModelScope.launch {
            interactor.getPlaylist(playlistId)
                .distinctUntilChanged()
                .flatMapLatest { playlist ->
                    interactor.getTrackInPlaylist(playlist.tracks.asReversed())
                        .map { tracks -> playlist to tracks }
                }
                .collect { (playlist, tracks) ->
                    processResult(playlist, tracks)
                }
        }
    }

    private fun processResult(foundPlaylist: Playlist, tracks: List<Track>) {
        if (foundPlaylist.name.isNotEmpty()) {
            renderState(PlaylistCardState.Content(foundPlaylist, tracks))
            playlistG = foundPlaylist
            tracksG = tracks
        } else {
            renderState(PlaylistCardState.Empty("Плейлист не найден"))
        }
    }

    private fun renderState(state: PlaylistCardState) {
        playlistCardStateLiveData.postValue(state)
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistG?.let { playlist ->
                interactor.deleteTrackFromPlaylist(playlist, track)
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistG?.let { playlist ->
                interactor.deletePlaylist(playlist)
            }
        }
    }

    fun sharePlaylist() {
        viewModelScope.launch {
            playlistG?.let { playlist ->
                interactor.sharePlaylist(playlist, tracksG)
            }
        }
    }
}