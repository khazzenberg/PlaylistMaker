package com.practicum.playlistmaker.creator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class TracksApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        val darkTheme = settingsInteractor.getThemeSettings().isDarkTheme
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}