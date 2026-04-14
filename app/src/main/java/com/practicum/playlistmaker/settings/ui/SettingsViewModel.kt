package com.practicum.playlistmaker.settings.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import androidx.lifecycle.viewmodel.initializer
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val stateLiveData = (
            ThemeSettings(settingsInteractor.getThemeSettings().isDarkTheme))

    fun observeState(): LiveData<ThemeSettings> = MutableLiveData(
        ThemeSettings(settingsInteractor.getThemeSettings().isDarkTheme)
    )

    private val handler = Handler(Looper.getMainLooper())

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun changeTheme(b: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(b))
    }

    fun getTheme(): Boolean {
        return settingsInteractor.getThemeSettings().isDarkTheme
    }


    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SETTINGS_REQUEST_TOKEN)
    }

    companion object {
        private val SETTINGS_REQUEST_TOKEN = Any()
        fun getFactory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }
}