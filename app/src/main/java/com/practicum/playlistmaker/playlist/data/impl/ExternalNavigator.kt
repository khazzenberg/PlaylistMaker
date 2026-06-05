package com.practicum.playlistmaker.playlist.data.impl

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(getShareAppLink: String)
    fun openTerms(getTermsLink: String)
    fun openEmail(getSupportEmailData: EmailData)
}