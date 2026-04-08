package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList

private const val MAX_SIZE = 10

class SearchHistoryStorage(val sharedPreferences: SharedPreferences){
    private val gson: Gson = Gson()

    fun getHistory() : List<TrackHistoryDto> {
        val json = sharedPreferences.getString(SEARCH_TRACK_HISTORY_KEY,null)
        if (json == null) return emptyList()
        return createHistoryFromJson(json)
    }

    fun addTrackToHistory(track: TrackHistoryDto) {
        val list = getHistory().toMutableList()

        list.removeAll{track.trackId == it.trackId}
        if (list.size >= MAX_SIZE) {
            list.removeAt(list.lastIndex)
        }
        list.add(0,track)
        sharedPreferences.edit()
            .putString(SEARCH_TRACK_HISTORY_KEY, createJsonFromHistory(list))
            .apply()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(SEARCH_TRACK_HISTORY_KEY).apply()
    }

    private fun createJsonFromHistory(history: List<TrackHistoryDto>): String {
        return gson.toJson(history)
    }

    private fun createHistoryFromJson(json: String): MutableList<TrackHistoryDto> {
        val type = object : TypeToken<List<TrackHistoryDto>>() {}.type
        return gson.fromJson(json, type)
    }

    companion object {
        const val SEARCH_TRACK_HISTORY_KEY = "searchTrackHistory"
    }
}