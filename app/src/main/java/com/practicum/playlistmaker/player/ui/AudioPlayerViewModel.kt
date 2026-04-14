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
import com.practicum.playlistmaker.player.domain.models.PlayerLiveData
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val track: Track) : ViewModel() {
    val playerLiveData = MutableLiveData<PlayerLiveData>()
    private var playerState = STATE_DEFAULT
    //fun observePlayerState(): LiveData<Int> = playerStateLiveData
    fun observePlayerVars(newPlayerLiveData: PlayerLiveData) {
        playerLiveData.value = newPlayerLiveData
        playerLiveData.postValue(newPlayerLiveData)
    }
    private var progressTime = "00:00"
    //fun observeProgressTime(): LiveData<String> = progressTimeLiveData
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerState == STATE_PLAYING) {
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
            playerState = STATE_PREPARED
            observePlayerVars(getCurrentPlayerLiveData())
            //playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            observePlayerVars(getCurrentPlayerLiveData())
            //playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        //playerStateLiveData.postValue(STATE_PLAYING)
        playerState = STATE_PLAYING
        observePlayerVars(getCurrentPlayerLiveData())
        startTimerUpdate()
    }

    fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        //playerStateLiveData.postValue(STATE_PAUSED)
        playerState = STATE_PAUSED
        observePlayerVars(getCurrentPlayerLiveData())
    }

    private fun startTimerUpdate() {
        /*progressTimeLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        )*/
        progressTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        observePlayerVars(getCurrentPlayerLiveData())
        mainThreadHandler?.postDelayed(timerRunnable, REFRESH_TIMER_DELAY_MILLS)
    }

    private fun pauseTimer() {
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        mainThreadHandler?.removeCallbacks(timerRunnable)
        //progressTimeLiveData.postValue("00:00")
        progressTime = "00:00"
        observePlayerVars(getCurrentPlayerLiveData())
    }

    private fun getCurrentPlayerLiveData(): PlayerLiveData {
        val currentPlayerLiveData = PlayerLiveData(playerState, progressTime)
        return currentPlayerLiveData
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