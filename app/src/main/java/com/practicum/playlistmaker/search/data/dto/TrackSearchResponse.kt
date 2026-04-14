package com.practicum.playlistmaker.search.data.dto

class TrackSearchResponse (val resultCount: Int,
                           val expression: String,
                           val results: List<TrackDto>) : Response()