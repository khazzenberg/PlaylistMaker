package com.practicum.playlistmaker.ui.audioplayer

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity(R.layout.activity_audio_player) {
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var url: String
    private lateinit var play: ImageView
    private lateinit var pause: ImageView
    private lateinit var trackTimeCurrent: TextView
    private var mainThreadHandler: Handler? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainThreadHandler = Handler(Looper.getMainLooper())
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu_button)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setContentView(R.layout.activity_audio_player)
        val toolbar = findViewById<Toolbar>(R.id.menu_button)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val imageAlbum = findViewById<ImageView>(R.id.imageAlbum)
        val trackName = findViewById<TextView>(R.id.trackName)
        val trackArtist = findViewById<TextView>(R.id.trackArtist)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val trackGenre = findViewById<TextView>(R.id.trackGenre)
        val trackCountry = findViewById<TextView>(R.id.trackCountry)

        trackTimeCurrent = findViewById(R.id.trackTimeCurrent)
        play = findViewById(R.id.play)
        pause = findViewById(R.id.pause)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA)
        }

        if (track == null) {
            finish()
            return
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_312)
            .transform(RoundedCorners(dpToPx(8f)))
            .into(imageAlbum)
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackTime.text = track.getFormattedTime()
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country
        url = track.previewUrl

        visibleText(track)
        preparePlayer()

        play.setOnClickListener {
            playbackControl()
        }
        pause.setOnClickListener {
            pausePlayer()
        }
    }

    private fun visibleText(track: Track) {
        val trackYear = findViewById<TextView>(R.id.trackYear)
        val year = findViewById<TextView>(R.id.year)
        val album = findViewById<TextView>(R.id.album)
        val trackAlbum = findViewById<TextView>(R.id.trackAlbum)

        trackYear.visibility = View.GONE
        year.visibility = View.GONE
        trackAlbum.visibility = View.GONE
        album.visibility = View.GONE

        if (!track.releaseDate.isNullOrEmpty()) {
            trackYear.text = track.releaseDate?.take(4)
            trackYear.visibility = View.VISIBLE
            year.visibility = View.VISIBLE
        }

        if (!track.collectionName.isNullOrEmpty()) {
            trackAlbum.text = track.collectionName
            trackAlbum.visibility = View.VISIBLE
            album.visibility = View.VISIBLE
        }
    }
    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics).toInt()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            pause.visibility = View.GONE
            play.visibility = View.VISIBLE
            mainThreadHandler?.removeCallbacks(refreshStopWatch)
            trackTimeCurrent.text = dateFormat.format(0)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(refreshStopWatch)
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        pause.visibility = View.VISIBLE
        play.visibility = View.GONE
        mainThreadHandler?.post(refreshStopWatch)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        //смена времени
        pause.visibility = View.GONE
        play.visibility = View.VISIBLE
        mainThreadHandler?.removeCallbacks(refreshStopWatch)
    }

    private fun updateCurrentTime() {
        trackTimeCurrent.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private var refreshStopWatch = object : Runnable {
        override fun run() {
            mainThreadHandler?.postDelayed(this, REFRESH_TIMER_DELAY_MILLS)
            updateCurrentTime()
        }
    }

    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TIMER_DELAY_MILLS = 300L
    }
}