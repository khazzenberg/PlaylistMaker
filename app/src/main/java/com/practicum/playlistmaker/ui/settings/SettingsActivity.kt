package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Creator

class SettingsActivity : AppCompatActivity() {
    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val shareBlock = findViewById<LinearLayout>(R.id.shareBlock)
        shareBlock.setOnClickListener {
            share()
        }

        val supportBlock = findViewById<LinearLayout>(R.id.supportBlock)
        supportBlock.setOnClickListener {
            val email = getString(R.string.support_email)
            val subject = getString(R.string.support_subject)
            val body = getString(R.string.support_body)
            sendEmail(email, subject, body)
        }

        val userAgreementBlock = findViewById<LinearLayout>(R.id.userAgreementBlock)
        userAgreementBlock.setOnClickListener {
            goToUserAgreement()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = settingsInteractor.isDarkThemeEnabled()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            settingsInteractor.setDarkTheme(checked)
            (applicationContext as App).switchTheme(checked)
        }
    }

    private fun share() {
        val shareUrl = getString(R.string.share_url)
        val shareMessage = getString(R.string.share_learn_android_text) + "\n$shareUrl"
        val shareSubject = getString(R.string.share_subject)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, shareSubject)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        val shareBy = getString(R.string.share_by_text)
        startActivity(Intent.createChooser(shareIntent, shareBy))
    }

    private fun sendEmail(email : String, subject : String, body : String) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(supportIntent)
    }

    private fun goToUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
        startActivity(intent)
    }
}