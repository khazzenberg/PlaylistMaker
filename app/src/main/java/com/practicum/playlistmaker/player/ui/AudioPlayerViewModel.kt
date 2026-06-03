package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favoritetracks.domain.db.LikeInteractor
import com.practicum.playlistmaker.player.AudioPlayerState
import com.practicum.playlistmaker.player.presentation.LikeState
import com.practicum.playlistmaker.player.presentation.PlaylistPlayerState
import com.practicum.playlistmaker.player.presentation.TrackInPlaylistState
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val likeInteractor: LikeInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<AudioPlayerState>(AudioPlayerState.Default())
    fun observePlayerState(): LiveData<AudioPlayerState> = playerStateLiveData
    private val likeStateLiveData = MutableLiveData<LikeState>()
    fun observeLikeState(): LiveData<LikeState> = likeStateLiveData

    private val playlistStateLiveData = MutableLiveData<PlaylistPlayerState>()
    fun observePlaylistState(): LiveData<PlaylistPlayerState> = playlistStateLiveData
    private val trackInPlaylistStateLiveData = MutableLiveData<TrackInPlaylistState>()
    fun observeTrackInPlaylistState(): LiveData<TrackInPlaylistState> = trackInPlaylistStateLiveData

    init {
        preparePlayer()
        checkLike()
    }

    fun fillPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists()
                .collect { playlists -> processResultPlaylist(playlists) }
        }
    }

    fun processResultPlaylist(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderStatePlaylist(PlaylistPlayerState.Empty(""))
        } else {
            renderStatePlaylist(PlaylistPlayerState.Content(playlists))
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            is AudioPlayerState.Playing -> {
                pausePlayer()
            }
            is AudioPlayerState.Paused, is AudioPlayerState.Prepared -> {
                startPlayer()
            }
            is AudioPlayerState.Default -> {
                mediaPlayer.stop()
            }
            else -> {}
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(AudioPlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(AudioPlayerState.Prepared())
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(AudioPlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(AudioPlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer(){
        mediaPlayer.stop()
        mediaPlayer.release()
        playerStateLiveData.value = AudioPlayerState.Default()
    }

    fun onLikeClicked() {
        viewModelScope.launch {
            if (track.isLike)
                likeInteractor.unlikeTrack(track)
            else
                likeInteractor.likeTrack(track)
            track.isLike = !track.isLike
            likeStateLiveData.postValue(LikeState(track.isLike))
        }
    }

    fun checkLike() {
        viewModelScope.launch {
            val isLikeTrackId = likeInteractor.getTrackId(track)
            if (track.trackId == isLikeTrackId) {
                track.isLike = true
            } else {
                track.isLike = false
            }
            likeStateLiveData.postValue(LikeState(track.isLike))
        }
    }

    private fun startTimer(){
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(REFRESH_TIMER_DELAY_MILLS)
                playerStateLiveData.postValue(AudioPlayerState.Playing(getCurrentPlayerPosition()))
            }
            playerStateLiveData.postValue(AudioPlayerState.Prepared())
        }
    }

    fun renderStatePlaylist(state: PlaylistPlayerState) {
        playlistStateLiveData.postValue(state)
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    fun addTrackToPlaylist(playlist: Playlist,track: Track){
        viewModelScope.launch {
            if (playlist.tracks.contains(track.trackId))
                trackInPlaylistStateLiveData.postValue(TrackInPlaylistState(playlist.name,true))
            else {
                playlistInteractor.insertTrackInPlaylist(playlist, track)
                trackInPlaylistStateLiveData.postValue(TrackInPlaylistState(playlist.name,false))
            }
        }
    }

    companion object {
        const val REFRESH_TIMER_DELAY_MILLS = 300L
    }
}