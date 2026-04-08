package com.practicum.playlistmaker.domain.api

interface SettingsRepository {
    fun saveDarkTheme(enabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
}