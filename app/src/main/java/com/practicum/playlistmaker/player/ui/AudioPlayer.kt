package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayer : AppCompatActivity(R.layout.activity_audio_player) {
    private val track: Track by lazy {
        Gson().fromJson(
            intent.getStringExtra(TRACK_EXTRA),
            Track::class.java
        )
    }
    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(track) }
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (track == null) {
            finish()
            return
        }

        viewModel.playerLiveData.observe(this, { playerLiveData ->
            changeButton(playerLiveData.state == PlayerState.PLAYING)
        })

        viewModel.playerLiveData.observe(this,{ playerLiveData ->
            binding.trackTimeCurrent.text = playerLiveData.progress
        })

        binding.menuButton.setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_312)
            .transform(RoundedCorners(dpToPx(8f)))
            .into(binding.imageAlbum)

        binding.apply {
            trackName.text = track.trackName
            trackArtist.text = track.artistName
            trackTime.text = track.getFormattedTime()
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
        }
        visibleText(track)

        binding.play.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.pause.setOnClickListener {
            viewModel.pausePlayer()
        }
    }

    private fun visibleText(track: Track) {
        binding.apply {
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
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun changeButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.VISIBLE
        } else {
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.GONE
        }

    }

    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"
    }
}