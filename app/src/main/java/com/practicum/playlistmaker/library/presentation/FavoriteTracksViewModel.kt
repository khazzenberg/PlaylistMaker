package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.LikeInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TracksState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val likeInteractor: LikeInteractor,
) : ViewModel() {
    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            likeInteractor.likeTracks().collect { tracks -> processResult(tracks) }
        }
    }

    fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(
                TracksState.Empty("")
            )
        } else {
            renderState(TracksState.Content(tracks))
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }
}