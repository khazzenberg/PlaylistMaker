package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val share = findViewById<LinearLayout>(R.id.shareBlock)
        share.setOnClickListener {
            viewModel.shareApp()
        }

        val support = findViewById<LinearLayout>(R.id.supportBlock)
        support.setOnClickListener {
            viewModel.openSupport()
        }

        val userAgreement = findViewById<LinearLayout>(R.id.userAgreementBlock)
        userAgreement.setOnClickListener {
            viewModel.openTerms()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        viewModel.observeState().observe(this) {state ->
            if (themeSwitcher.isChecked != state.isDarkTheme) {
                themeSwitcher.isChecked = state.isDarkTheme
            }
        }

        themeSwitcher.isChecked = viewModel.getTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)
            (applicationContext as App).switchTheme(checked)
        }
    }
}