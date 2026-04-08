package com.practicum.playlistmaker.data.dto

import android.content.SharedPreferences

class SettingsStorage(val sharedPreferences: SharedPreferences) {

    fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    fun setDarkTheme(darkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, darkTheme)
            .apply()
    }

    companion object {
        const val DARK_THEME_KEY = "darkTheme"
    }
}