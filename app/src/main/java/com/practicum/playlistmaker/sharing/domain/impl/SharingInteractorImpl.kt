package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingRepository

class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {

    override fun shareApp() {
        repository.shareApp()
    }

    override fun openTerms() {
        repository.openTerms()
    }

    override fun openSupport() {
        repository.openSupport()
    }
}