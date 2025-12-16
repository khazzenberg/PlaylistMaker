package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val SEARCH_TRACK_HISTORY_KEY = "searchTrackHistory"

class SearchActivity : AppCompatActivity() {
    private lateinit var inputSearchText: EditText
    private lateinit var buttonClearSearch: ImageView
    private lateinit var emptyState: LinearLayout
    private lateinit var errorState: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var trackListSearch: RecyclerView
    private lateinit var historyLayout : LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    private var currentText: String = ""
    private val searchTextKey: String = "search_text"
    private val trackService = RetrofitClient.iTunesService
    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksAdapter = TrackAdapter(tracks)
    private val tracksHistory: MutableList<Track> = mutableListOf()
    private val historyAdapter = TrackAdapter(tracksHistory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
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

        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonClearSearch.isVisible = !s.isNullOrEmpty()
                currentText = s.toString()

                if (currentText.isEmpty()) {
                    tracks.clear()
                    tracksAdapter.notifyDataSetChanged()

                    emptyState.visibility = View.GONE
                    errorState.visibility = View.GONE

                    historyLayout.visibility = View.VISIBLE
                    updateHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        inputSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (currentText.isNotEmpty()) {
                    search(currentText)
                }

                val imm = getSystemService<InputMethodManager>()
                imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)

                true
            } else {
                false
            }
        }

        buttonClearSearch.setOnClickListener {
            inputSearchText.text.clear()
            buttonClearSearch.isVisible = true

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
            searchHistory.addTrack(track)
            tracksHistory.clear()
            tracksHistory.addAll(searchHistory.getHistory())
            historyAdapter.notifyDataSetChanged()
            startPlayer(track)
        }

        historyAdapter.onTrackClick = { track ->
            startPlayer(track)
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            updateHistory()
        }

        inputSearchText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputSearchText.text.isEmpty()) {
                historyLayout.visibility = View.VISIBLE
                updateHistory()
            }
        }
    }

    private fun updateHistory() {
        val list = searchHistory.getHistory()
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
        trackService.searchSongs(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty()
                    if (results.isNotEmpty()) {
                        tracks.clear()
                        tracks.addAll(results)
                    }
                    tracksAdapter.notifyDataSetChanged()
                    historyLayout.visibility = View.GONE
                    if (tracks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showSearchResults()
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                tracks.clear()
                tracksAdapter.notifyDataSetChanged()
                showErrorState()
            }
        })
    }

    private fun showSearchResults() {
        errorState.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    private fun showErrorState() {
        errorState.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun showEmptyState() {
        errorState.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
    }
}