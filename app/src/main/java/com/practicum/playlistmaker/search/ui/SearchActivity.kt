package com.practicum.playlistmaker.search.ui

import com.google.gson.Gson
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.AudioPlayer
import com.practicum.playlistmaker.search.ui.models.TracksState

class SearchActivity : AppCompatActivity() {
    private var viewModel: SearchViewModel? = null
    private lateinit var binding: ActivitySearchBinding
    private lateinit var simpleTextWatcher: TextWatcher
    private val tracks: MutableList<Track> = mutableListOf()
    private val adapter = TrackAdapter(tracks)
    private val tracksHistory: MutableList<Track> = mutableListOf()
    private val historyAdapter = TrackAdapter(tracksHistory)
    private var constTextEdit: String = TEXT_EDIT_VALUE
    private var constIsClearButtonVisible: Int = View.GONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            render(it)
        }

        if (savedInstanceState != null) {
            constTextEdit = savedInstanceState.getString(EDIT_TEXT, TEXT_EDIT_VALUE)
            constIsClearButtonVisible = savedInstanceState.getInt(IS_VISIBLE_BUTTON, 0)
        }
        binding.inputSearchText.setText(constTextEdit.toString())
        binding.searchClearButton.visibility = constIsClearButtonVisible

        binding.back.setOnClickListener {
            finish()
        }

        binding.trackList.adapter = adapter
        binding.trackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter.onTrackClick = { track ->
            viewModel?.onCLickTrack(track)
            startAudioPlayerActivity(track)
        }

        binding.historyList.adapter = historyAdapter
        historyAdapter.onTrackClick = { track ->
            viewModel?.onCLickTrack(track)
            startAudioPlayerActivity(track)
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel?.searchDebounce(changedText = s?.toString() ?: "")
                constTextEdit = binding.inputSearchText.text.toString()
                binding.searchClearButton.visibility = clearButtonVisibility(s)
                constIsClearButtonVisible = binding.searchClearButton.visibility

                if (binding.inputSearchText.hasFocus() && s?.isEmpty() == true) {
                    viewModel?.loadHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.inputSearchText.addTextChangedListener(simpleTextWatcher)

        binding.updateButton.setOnClickListener {
            viewModel?.searchDebounce(binding.inputSearchText.text.toString(), true)
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel?.clearHistory()
        }
        binding.searchClearButton.setOnClickListener {
            binding.inputSearchText.setText("")
            closeKeyboard()
        }

        binding.inputSearchText.setOnFocusChangeListener { view, hasFocus ->
            viewModel?.searchDebounce(binding.inputSearchText.text.toString())
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
        binding.apply {
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
        binding.apply {
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
        binding.apply {
            errorState.visibility = View.GONE
            emptyState.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            trackList.visibility = View.GONE
            historyLayout.visibility = View.GONE
        }
    }

    fun showError() {
        binding.apply {
            errorState.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            trackList.visibility = View.GONE
            progressBar.visibility = View.GONE
            closeKeyboard()
        }
    }

    fun showEmpty() {
        binding.apply {
            errorState.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
            trackList.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
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

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Error -> showError()
            is TracksState.Empty -> showEmpty()
            is TracksState.HistoryContent -> showHistory(state.tracks)
            is TracksState.Content -> showContent(state.tracks)
        }
    }

    fun startAudioPlayerActivity(track: Track) {
        val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
        audioPlayerIntent.putExtra(AudioPlayer.Companion.TRACK_EXTRA, Gson().toJson(track))
        startActivity(audioPlayerIntent)
    }

    companion object {
        private const val EDIT_TEXT = "EDIT_TEXT"
        private const val TEXT_EDIT_VALUE = ""
        private const val IS_VISIBLE_BUTTON = "IS_VISIBLE_BUTTON"
    }
}