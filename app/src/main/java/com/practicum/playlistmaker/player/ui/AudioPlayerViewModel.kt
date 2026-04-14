package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val track: Track) : ViewModel() {
    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData
    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
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
        when (playerStateLiveData.value) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startTimerUpdate() {
        progressTimeLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        )
        mainThreadHandler?.postDelayed(timerRunnable, REFRESH_TIMER_DELAY_MILLS)
    }

    private fun pauseTimer() {
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        mainThreadHandler?.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue("00:00")
    }


    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val REFRESH_TIMER_DELAY_MILLS = 200L
        fun getFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(track)
            }
        }
    }
}