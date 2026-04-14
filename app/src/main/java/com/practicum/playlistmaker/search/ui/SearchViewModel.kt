package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.creator.TracksApplication
import com.practicum.playlistmaker.player.domain.api.TracksInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.ui.models.TracksState

class SearchViewModel(private val context: Context) : ViewModel() {
    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(context)
    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData
    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    fun searchDebounce(changedText: String, force: Boolean = false) {
        if (!force && latestSearchText == changedText) return

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { search(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)
            tracksInteractor.searchTracks(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        handler.post {
                            if (latestSearchText != newSearchText) {
                                return@post
                            }
                            val tracks = mutableListOf<Track>()
                            if (foundTracks != null) {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                            }
                            when {
                                (foundTracks?.isEmpty() == true) -> {
                                    renderState(
                                        TracksState.Empty("")
                                    )
                                }

                                (errorMessage != null) -> {
                                    renderState(
                                        TracksState.Error("")
                                    )
                                }

                                else -> {
                                    renderState(
                                        TracksState.Content(
                                            tracks = tracks,
                                        )
                                    )
                                }
                            }

                        }
                    }
                })
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun clearHistory() {
        searchHistoryInteractor.clear()
        loadHistory()
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.add(track)
    }

    fun loadHistory() {
        searchHistoryInteractor.get(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                val tracksHistory = mutableListOf<Track>()
                tracksHistory.clear()
                tracksHistory.addAll(searchHistory ?: emptyList())
                renderState(
                    TracksState.HistoryContent(
                        tracks = searchHistory ?: emptyList()
                    )
                )
            }
        })
    }

    fun onCLickTrack(track: Track) {
        if (clickDebounce()) {
            saveTrackToHistory(track)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as TracksApplication)
                SearchViewModel(app)
            }
        }

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}