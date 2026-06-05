package com.practicum.playlistmaker.playlist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String,
    val image: String,
    var tracks: MutableList<Int>,
    var count: Int
) : Parcelable