package com.practicum.playlistmaker.ui.tracks

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Creator
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.audioplayer.AudioPlayer

class SearchActivity : AppCompatActivity() {
    private lateinit var inputSearchText: EditText
    private lateinit var buttonClearSearch: ImageView
    private lateinit var emptyState: LinearLayout
    private lateinit var errorState: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var trackListSearch: RecyclerView
    private lateinit var historyLayout : LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private var currentText: String = ""
    private val searchTextKey: String = "search_text"
    private lateinit var loadTracksInteractor: TracksInteractor
    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksAdapter = TrackAdapter(tracks)
    private val tracksHistory: MutableList<Track> = mutableListOf()
    private val historyAdapter = TrackAdapter(tracksHistory)
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search(currentText) }
    private val searchHistoryInteractor by lazy { Creator.provideSearchTracksInteractor(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadTracksInteractor = Creator.provideTracksInteractor()
        setContentView(R.layout.activity_search)

        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        inputSearchText = findViewById<EditText>(R.id.inputSearchText)
        buttonClearSearch = findViewById(R.id.buttonClearSearch)
        emptyState = findViewById(R.id.emptyState)
        errorState = findViewById(R.id.errorState)
        updateButton = findViewById(R.id.updateButton)
        historyLayout = findViewById<LinearLayout>(R.id.historyLinearLayout)
        clearHistoryButton = findViewById(R.id.clearButton)
        progressBar = findViewById(R.id.progressBar)

        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                buttonClearSearch.isVisible = !s.isNullOrEmpty()
                currentText = s.toString()

                if (currentText.isEmpty()) {
                    hide()
                    tracks.clear()
                    tracksAdapter.notifyDataSetChanged()
                    historyLayout.visibility = View.VISIBLE
                    updateHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        inputSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                search(currentText)

                val imm = getSystemService<InputMethodManager>()
                imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)

                true
            } else {
                false
            }
        }

        buttonClearSearch.setOnClickListener {
            inputSearchText.text.clear()
            inputSearchText.clearFocus()
            buttonClearSearch.isVisible = true

            hide()

            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
        }

        updateButton.setOnClickListener {
            search(currentText)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tracksAdapter

        trackListSearch = findViewById(R.id.recyclerHistoryView)
        trackListSearch.adapter = historyAdapter
        tracksAdapter.onTrackClick = { track ->
            searchHistoryInteractor.add(track)
            tracksHistory.clear()
            tracksHistory.addAll(searchHistoryInteractor.get())
            historyAdapter.notifyDataSetChanged()
            if (clickDebounce()) {
                startPlayer(track)
            }
        }

        historyAdapter.onTrackClick = { track ->
            startPlayer(track)
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clear()
            updateHistory()
        }

        inputSearchText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputSearchText.text.isEmpty()) {
                historyLayout.visibility = View.VISIBLE
                updateHistory()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun updateHistory() {
        val list = searchHistoryInteractor.get()
        if (list.isEmpty()) {
            historyLayout.visibility = View.GONE
        }
        tracksHistory.clear()
        tracksHistory.addAll(list)
        historyAdapter.notifyDataSetChanged()
    }

    private fun startPlayer(track: Track) {
        val audioPlayerIntent = Intent(this, AudioPlayer::class.java)
        audioPlayerIntent.putExtra(AudioPlayer.TRACK_EXTRA, track)
        startActivity(audioPlayerIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(searchTextKey, currentText)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(searchTextKey, "")
        inputSearchText.setText(restoredText)
    }

    private fun search(query: String) {
        if (query.isEmpty()) {
            return
        }

        hide()
        progressBar.visibility = View.VISIBLE
        loadTracksInteractor.searchTracks(
            currentText,
            object : TracksInteractor.TracksConsumer {
                override fun consume(result: Result<List<Track>>) {
                    runOnUiThread {
                        result.onSuccess { foundTracks ->
                            if (foundTracks.isEmpty()) {
                                tracks.clear()
                                tracksAdapter.notifyDataSetChanged()
                                showEmptyState()
                            } else {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                                tracksAdapter.notifyDataSetChanged()
                                showSearchResults()
                            }
                        }.onFailure {
                            tracks.clear()
                            tracksAdapter.notifyDataSetChanged()
                            showErrorState()
                        }
                    }
                }
            })
    }

    private fun showSearchResults() {
        progressBar.visibility = View.GONE
    }

    private fun showErrorState() {
        trackListSearch.visibility = View.GONE
        errorState.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showEmptyState() {
        trackListSearch.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun hide() {
        trackListSearch.visibility = View.VISIBLE
        errorState.visibility = View.GONE
        emptyState.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}