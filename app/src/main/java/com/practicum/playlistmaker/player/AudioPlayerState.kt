package com.practicum.playlistmaker.player

sealed class AudioPlayerState(val isPlayButtonEnabled: Boolean, val progress: String) {
    class Playing (progress: String): AudioPlayerState(false, progress)
    class Prepared : AudioPlayerState(true, "00:00")
    class Paused (progress: String): AudioPlayerState(true, progress)
    class Default : AudioPlayerState(true, "00:00")
}