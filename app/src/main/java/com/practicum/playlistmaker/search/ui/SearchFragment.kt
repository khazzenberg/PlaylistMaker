package com.practicum.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TracksState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {
    private val gson: Gson by inject()
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var simpleTextWatcher: TextWatcher
    private val tracks: MutableList<Track> = mutableListOf()
    private val adapter = TrackAdapter(tracks)
    private val tracksHistory: MutableList<Track> = mutableListOf()
    private val historyAdapter = TrackAdapter(tracksHistory)
    private var constTextEdit: String = TEXT_EDIT_VALUE
    private var constIsClearButtonVisible: Int = View.GONE
    private var _binding: FragmentSearchBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        if (savedInstanceState != null) {
            constTextEdit = savedInstanceState.getString(
                EDIT_TEXT,
                TEXT_EDIT_VALUE
            )
            constIsClearButtonVisible = savedInstanceState.getInt(IS_VISIBLE_BUTTON, 0)
        }

        _binding?.inputSearchText?.setText(constTextEdit)
        _binding?.searchClearButton?.visibility = constIsClearButtonVisible

        _binding?.trackList?.adapter = adapter
        _binding?.trackList?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapter.onTrackClick = { track ->
            viewModel.onCLickTrack(track)
            findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(gson.toJson(track)))
        }

        _binding?.historyList?.adapter = historyAdapter
        historyAdapter.onTrackClick = { track ->
            viewModel.onCLickTrack(track)
            findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(gson.toJson(track)))
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
                constTextEdit = _binding?.inputSearchText?.text.toString()
                _binding?.searchClearButton?.visibility = clearButtonVisibility(s)
                constIsClearButtonVisible = _binding?.searchClearButton?.visibility!!

                if (_binding?.inputSearchText?.hasFocus()!! && s?.isEmpty() == true) {
                    viewModel.loadHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        _binding?.inputSearchText?.addTextChangedListener(simpleTextWatcher)

        _binding?.updateButton?.setOnClickListener {
            viewModel.searchDebounce(_binding?.inputSearchText?.text.toString(), true)
        }

        _binding?.clearHistoryButton?.setOnClickListener {
            viewModel.clearHistory()
        }

        _binding?.searchClearButton?.setOnClickListener {
            _binding?.inputSearchText?.setText("")
            closeKeyboard()
        }

        _binding?.inputSearchText?.setOnFocusChangeListener { view, hasFocus ->
            viewModel.searchDebounce(_binding?.inputSearchText?.text.toString())
            if (hasFocus && (view as EditText).text.isEmpty()) {
                viewModel.loadHistory()
            }
        }
    }

    fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun showContent(foundTrack: List<Track>) {
        _binding?.apply {
            errorState.visibility = View.GONE
            emptyState.visibility = View.GONE
            progressBar.visibility = View.GONE
            trackList.visibility = View.VISIBLE
            historyLayout.visibility = View.GONE
            closeKeyboard()
            tracks.clear()
            tracks.addAll(foundTrack)
            adapter.notifyDataSetChanged()
        }
    }

    fun showHistory(foundTrack: List<Track>) {
        _binding?.apply {
            errorState.visibility = View.GONE
            emptyState.visibility = View.GONE
            progressBar.visibility = View.GONE
            trackList.visibility = View.GONE
            if (foundTrack.isEmpty()) {
                historyLayout.visibility = View.GONE
            } else {
                historyLayout.visibility = View.VISIBLE
                tracksHistory.clear()
                tracksHistory.addAll(foundTrack)
            }
            historyAdapter.notifyDataSetChanged()
        }
    }

    fun showLoading() {
        _binding?.apply {
            errorState.visibility = View.GONE
            emptyState.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            trackList.visibility = View.GONE
            historyLayout.visibility = View.GONE
        }
    }

    fun showError() {
        _binding?.apply {
            errorState.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            trackList.visibility = View.GONE
            progressBar.visibility = View.GONE
            closeKeyboard()
        }
    }

    fun showEmpty() {
        _binding?.apply {
            errorState.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
            trackList.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun closeKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, constTextEdit)
        outState.putInt(
            IS_VISIBLE_BUTTON,
            constIsClearButtonVisible
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let { _binding?.inputSearchText?.removeTextChangedListener(it) }//??
    }

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Error -> showError()
            is TracksState.Empty -> showEmpty()
            is TracksState.HistoryContent -> showHistory(state.tracks)
            is TracksState.Content -> showContent(state.tracks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EDIT_TEXT = "EDIT_TEXT"
        private const val TEXT_EDIT_VALUE = ""
        private const val IS_VISIBLE_BUTTON = "IS_VISIBLE_BUTTON"
    }
}