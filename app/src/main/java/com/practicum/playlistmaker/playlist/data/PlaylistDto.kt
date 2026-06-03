package com.practicum.playlistmaker.playlist.data

class PlaylistDto(
    val name: String,
    val description: String,
    val image: String,
    val tracks: List<Int>,
    val count: Int
)