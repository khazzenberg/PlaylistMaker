package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.SettingsStorage
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(val settingsStorage: SettingsStorage): SettingsRepository {
    override fun saveDarkTheme(enabled: Boolean) {
        settingsStorage.setDarkTheme(enabled)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return settingsStorage.isDarkThemeEnabled()
    }
}