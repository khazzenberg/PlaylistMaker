package com.practicum.playlistmaker.player.domain.models

data class PlayerLiveData (
    val state: PlayerState,
    val progress: String
)