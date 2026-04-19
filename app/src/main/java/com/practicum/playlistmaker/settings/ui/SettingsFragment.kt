package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _binding?.shareBlock?.setOnClickListener {
            viewModel.shareApp()
        }

        _binding?.supportBlock?.setOnClickListener {
            viewModel.openSupport()
        }
        _binding?.userAgreementBlock?.setOnClickListener {
            viewModel.openTerms()
        }


        viewModel.observeState().observe(viewLifecycleOwner) {state ->
            if (_binding?.themeSwitcher?.isChecked != state.isDarkTheme) {
                _binding?.themeSwitcher?.isChecked = state.isDarkTheme
            }
        }

        _binding?.themeSwitcher?.isChecked = viewModel.getTheme()
        _binding?.themeSwitcher?.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)
            (requireActivity().application as App).switchTheme(checked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}