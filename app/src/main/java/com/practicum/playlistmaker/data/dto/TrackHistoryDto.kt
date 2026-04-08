package com.practicum.playlistmaker.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackHistoryDto (
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable