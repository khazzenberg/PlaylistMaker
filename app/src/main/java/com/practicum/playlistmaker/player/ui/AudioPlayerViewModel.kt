package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.models.PlayerLiveData
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {
    val playerLiveData = MutableLiveData<PlayerLiveData>()
    private var playerState = PlayerState.DEFAULT
    fun observePlayerVars(newPlayerLiveData: PlayerLiveData) {
        playerLiveData.value = newPlayerLiveData
        playerLiveData.postValue(newPlayerLiveData)
    }
    private var progressTime = "00:00"
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerState == PlayerState.PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPlayButtonClicked() {
        when (playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }

            PlayerState.DEFAULT -> {
                mediaPlayer.stop()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
            observePlayerVars(getCurrentPlayerLiveData())
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            observePlayerVars(getCurrentPlayerLiveData())
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        observePlayerVars(getCurrentPlayerLiveData())
        startTimerUpdate()
    }

    fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        observePlayerVars(getCurrentPlayerLiveData())
    }

    private fun startTimerUpdate() {
        progressTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        observePlayerVars(getCurrentPlayerLiveData())
        mainThreadHandler?.postDelayed(timerRunnable, REFRESH_TIMER_DELAY_MILLS)
    }

    private fun pauseTimer() {
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        mainThreadHandler?.removeCallbacks(timerRunnable)
        progressTime = "00:00"
        observePlayerVars(getCurrentPlayerLiveData())
    }

    private fun getCurrentPlayerLiveData(): PlayerLiveData {
        val currentPlayerLiveData = PlayerLiveData(playerState, progressTime)
        return currentPlayerLiveData
    }

    companion object {
        const val REFRESH_TIMER_DELAY_MILLS = 200L
    }
}