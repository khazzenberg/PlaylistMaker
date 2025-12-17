package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AudioPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        toolbar.setNavigationOnClickListener {
            finish()
        }


        val track: Track? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA)
        }

        if (track == null) {
            finish()
            return
        }

        val imageAlbum = findViewById<ImageView>(R.id.imageAlbum)
        val trackName = findViewById<TextView>(R.id.trackName)
        val trackArtist = findViewById<TextView>(R.id.trackArtist)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val trackGenre = findViewById<TextView>(R.id.trackGenre)
        val trackCountry = findViewById<TextView>(R.id.trackCountry)

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

        visibleText(track)
    }
    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"
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
}