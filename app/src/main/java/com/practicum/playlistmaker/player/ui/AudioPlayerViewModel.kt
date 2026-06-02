package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.LikeInteractor
import com.practicum.playlistmaker.player.AudioPlayerState
import com.practicum.playlistmaker.player.presentation.LikeState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val likeInteractor: LikeInteractor
) : ViewModel() {
    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<AudioPlayerState>(AudioPlayerState.Default())
    fun observePlayerState(): LiveData<AudioPlayerState> = playerStateLiveData
    private val likeStateLiveData = MutableLiveData<LikeState>()
    fun observeLikeState(): LiveData<LikeState> = likeStateLiveData

    init {
        preparePlayer()
        checkLike()
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

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    companion object {
        const val REFRESH_TIMER_DELAY_MILLS = 300L
    }
}