package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {
    private val gson: Gson by inject()
    private val track: Track by lazy {
        gson.fromJson(
            requireArguments().getString(TRACK_EXTRA),
            Track::class.java
        )?: error("Track is missing")
    }

    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(track) }

    private var _binding: FragmentAudioPlayerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playerLiveData.observe(viewLifecycleOwner, { playerLiveData ->
            changeButton(playerLiveData.state == PlayerState.PLAYING)
        })

        viewModel.playerLiveData.observe(viewLifecycleOwner,{ playerLiveData ->
            _binding?.trackTimeCurrent?.text = playerLiveData.progress
        })

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_312)
            .transform(RoundedCorners(dpToPx(8f)))
            .into(_binding?.imageAlbum!!)

        _binding?.apply {
            trackName.text = track.trackName
            trackArtist.text = track.artistName
            trackTime.text = track.getFormattedTime()
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
        }
        visibleText(track)

        _binding?.play?.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        _binding?.pause?.setOnClickListener {
            viewModel.pausePlayer()
        }

        _binding?.menuButton?.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun visibleText(track: Track) {
        _binding?.apply {
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

    private fun changeButton(isPlaying: Boolean) {
        if (isPlaying) {
            _binding?.play?.visibility = View.GONE
            _binding?.pause?.visibility = View.VISIBLE
        } else {
            _binding?.play?.visibility = View.VISIBLE
            _binding?.pause?.visibility = View.GONE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK_EXTRA = "TRACK_EXTRA"
        fun createArgs(track: String): Bundle = bundleOf(TRACK_EXTRA to track)
    }
}