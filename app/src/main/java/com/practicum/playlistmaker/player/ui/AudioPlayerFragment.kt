package com.practicum.playlistmaker.player.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.player.presentation.PlaylistPlayerState
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {
    private var _binding: FragmentAudioPlayerBinding? = null
    private var adapter: PlaylistPlayerAdapter? = null
    private val gson: Gson by inject()
    private val track: Track by lazy {
        gson.fromJson(
            requireArguments().getString(TRACK_EXTRA),
            Track::class.java
        )?: error("Track is missing")
    }

    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(track) }

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
            viewModel.onPlayButtonClicked()
        }

        _binding?.menuButton?.setOnClickListener{
            findNavController().navigateUp()
        }

        _binding?.like?.setOnClickListener { viewModel.onLikeClicked() }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            changeButton(it.isPlayButtonEnabled)
            _binding?.trackTimeCurrent?.text = it.progress
        }

        viewModel.observeLikeState().observe(viewLifecycleOwner) {
            if (it.isLike) {
                _binding?.like?.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red),
                    PorterDuff.Mode.SRC_IN)
                _binding?.like?.setImageResource(R.drawable.ic_like_active_25_23)
            } else {
                _binding?.like?.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white),
                    PorterDuff.Mode.SRC_IN)
                _binding?.like?.setImageResource(R.drawable.ic_like_25_23)
            }
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            renderPlaylist(it)
        }

        _binding?.overlay?.visibility = View.GONE

        val bottomSheetBehavior = BottomSheetBehavior.from(
            _binding?.playlistsBottomSheet?.let {
                it
            }
        ).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        _binding?.overlay?.visibility = View.GONE
                    }

                    else -> {
                        _binding?.overlay?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.observeTrackInPlaylistState().observe(viewLifecycleOwner) {
            if (it.inPlaylist) {
                Toast.makeText(
                    requireContext(),
                    "Трек уже добавлен в плейлист ${it.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                Toast.makeText(
                    requireContext(),
                    "Добавлено в плейлист ${it.name}",
                    Toast.LENGTH_SHORT
                ).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        _binding?.addToPl?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.fillPlaylist()
        }
        _binding?.newPlaylist?.setOnClickListener {
            findNavController().navigate(
                R.id.action_audioPlayerFragment_to_createPlaylistFragment
            )
        }
        adapter = PlaylistPlayerAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist, track)
        }

        _binding?.playlist?.adapter = adapter
        _binding?.playlist?.layoutManager = GridLayoutManager(
            requireContext(),
            1
        )

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
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

    fun renderPlaylist(state: PlaylistPlayerState) {
        when (state) {
            is PlaylistPlayerState.Content -> showContentPlaylist(state.playlists)
            is PlaylistPlayerState.Empty -> showEmptyPlaylist()
        }
    }

    private fun showContentPlaylist(playlists: List<Playlist>) {
        _binding?.playlist?.visibility = View.VISIBLE
        adapter?.playlists = playlists
        adapter?.notifyDataSetChanged()
    }

    private fun showEmptyPlaylist() {
        _binding?.playlist?.visibility = View.GONE
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    private fun changeButton(isPlayButtonEnabled: Boolean) {
        if (isPlayButtonEnabled) {
            _binding?.play?.visibility = View.VISIBLE
            _binding?.pause?.visibility = View.GONE
        } else {
            _binding?.play?.visibility = View.GONE
            _binding?.pause?.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        private const val TRACK_EXTRA = "TRACK_EXTRA"
        fun createArgs(track: String): Bundle = bundleOf(TRACK_EXTRA to track)
    }
}