package com.practicum.playlistmaker.sharing.data.storage

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(getShareAppLink: String)
    fun openLink(getTermsLink: String)
    fun openEmail(getSupportEmailData: EmailData)
}