package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.Collections.emptyList

private const val MAX_SIZE = 10

class SearchHistory (val sharedPreferences: SharedPreferences){
    fun getHistory() : MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_TRACK_HISTORY_KEY,null)
        if (json == null) return emptyList()
        return  createHistoryFromJson(json)
    }
    fun addTrack(track: Track) {
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

    fun clear() {
        sharedPreferences.edit().remove(SEARCH_TRACK_HISTORY_KEY).apply()
    }

    private fun createJsonFromHistory(history: List<Track>): String {
        return Gson().toJson(history)
    }

    private fun createHistoryFromJson(json: String): MutableList<Track> {
        val type = object : com.google.gson.reflect.TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }
}