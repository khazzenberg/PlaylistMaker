package com.practicum.playlistmaker.domain.api

interface SettingsInteractor {
    fun setDarkTheme(enabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
}