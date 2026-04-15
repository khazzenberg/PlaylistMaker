package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.sharing.data.storage.ExternalNavigator

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareLink(getShareAppLink: String) {
        val shareMessage = context.getString(R.string.share_learn_android_text) + "\n$getShareAppLink"
        val shareSubject = context.getString(R.string.share_subject)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

        val shareBy = context.getString(R.string.share_by_text)
        context.startActivity(
            Intent.createChooser(shareIntent, shareBy)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(getTermsLink: String) {
        val uri = getTermsLink.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openEmail(getSupportEmailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = context.getString(R.string.mail_to).toUri()
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getSupportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, getSupportEmailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, getSupportEmailData.text)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(supportIntent)
    }
}