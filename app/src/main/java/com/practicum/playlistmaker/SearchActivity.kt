package com.practicum.playlistmaker

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

class SearchActivity : AppCompatActivity() {
    private lateinit var inputSearchText: EditText
    private lateinit var buttonClearSearch: ImageView
    private lateinit var emptyState: LinearLayout
    private lateinit var errorState: LinearLayout
    private lateinit var updateButton: Button
    private var currentText: String = ""
    private val searchTextKey: String = "search_text"
    private val trackService = RetrofitClient.iTunesService
    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksAdapter = TrackAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val body = response.body()
                    tracks.clear()
                    body?.results?.forEach { result ->
                        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(result.trackTimeMillis)
                        tracks.add(
                            Track(
                                trackName = result.trackName,
                                artistName = result.artistName,
                                trackTime = formattedTime,
                                artworkUrl100 = result.artworkUrl100
                            )
                        )
                    }
                    tracksAdapter.notifyDataSetChanged()
                    if (tracks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showSearchResults()
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showErrorState()
            }
        })
    }

    private fun showSearchResults() {
        errorState.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    private fun showErrorState() {
        tracks.clear()
        errorState.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
    }

    private fun showEmptyState() {
        tracks.clear()
        errorState.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
    }
}