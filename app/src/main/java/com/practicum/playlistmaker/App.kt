package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLISTMAKER_PREFERENCES = "playlistmaker"
const val DARK_THEME_KEY = "darkTheme"

class App : Application() {
    var darkTheme: Boolean = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY,false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkTheme)
            .apply()
    }
}