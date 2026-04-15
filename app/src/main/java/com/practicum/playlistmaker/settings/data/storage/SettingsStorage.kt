package com.practicum.playlistmaker.settings.data.storage

interface SettingsStorage {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(darkTheme: Boolean)
}