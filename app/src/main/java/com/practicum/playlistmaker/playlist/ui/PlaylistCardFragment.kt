package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistCardBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.playlist.presentation.PlaylistCardState
import com.practicum.playlistmaker.playlist.presentation.PlaylistCardViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.debounce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class PlaylistCardFragment : Fragment() {
    private val gson: Gson by inject()
    private var _binding: FragmentPlaylistCardBinding? = null
    private val viewModel: PlaylistCardViewModel by viewModel() { parametersOf(playlistId) }
    val playlistId: Int by lazy {
        requireArguments().getInt(ARGS_PLAYLIST_ID, -1).takeIf { it != -1 }
            ?: error("Playlist ID is missing")
    }
    private lateinit var onTrackSearchDebounce: (Track) -> Unit
    private var adapter: TrackInPlaylistAdapter? = null
    private var adapterPlaylist: PlaylistCardAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistCardBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observePlaylistCardState().observe(viewLifecycleOwner) {
            render(it)
        }

        _binding?.menuButton?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        onTrackSearchDebounce =
            debounce<Track>(
                CLICK_DEBOUNCE_DELAY,
                viewLifecycleOwner.lifecycleScope,
                false
            ) { track ->
                findNavController().navigate(
                    R.id.action_playlistCardFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(gson.toJson(track))
                )
            }
        adapter = TrackInPlaylistAdapter(
            clickListener = { track -> onTrackSearchDebounce(track) },
            longClickListener = { track -> showDialog(track) })
        _binding?.trackList?.adapter = adapter
        _binding?.trackList?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        adapterPlaylist = PlaylistCardAdapter()
        _binding?.playlist?.adapter = adapterPlaylist
        _binding?.playlist?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        _binding?.overlay?.visibility = View.GONE
        val bottomSheetBehavior = BottomSheetBehavior.from(
            _binding?.playlistBottomSheet?.let {
                it
            }
        ).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        _binding?.share?.setOnClickListener {
            sharePlaylist()
        }
        _binding?.shareTextView?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }
        _binding?.menu?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        _binding?.overlay?.visibility = View.GONE
                        if (adapter?.tracks?.isEmpty() != true) {
                            _binding?.tracksBottomSheet?.visibility = View.VISIBLE
                        }
                    }

                    else -> {
                        _binding?.overlay?.visibility = View.VISIBLE
                        _binding?.tracksBottomSheet?.visibility = View.GONE

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        _binding?.deleteTextView?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext(), R.style.NewMaterialDialog)
                .setTitle("${getString(R.string.delete_playlist_header)}")
                .setMessage("${getString(R.string.delete_pl_descr)}")
                .setNeutralButton("${getString(R.string.cancel)}") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.delete_mes)) { dialog, which ->
                    viewModel.deletePlaylist()
                }
                .show()
        }
        _binding?.editTextView?.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistCardFragment_to_editPlaylistFragment
                , EditPlaylistFragment.createArgs( adapterPlaylist!!.playlists[0]))
        }
    }
    private fun showDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.NewMaterialDialog)
            .setTitle(getString(R.string.would_you_delete_track))
            .setNegativeButton(getString(R.string.no_message)) { dialog, which ->
            }
            .setPositiveButton((R.string.yes_message)) { dialog, which ->
                viewModel.deleteTrackFromPlaylist(track)
            }
            .show()
    }

    private fun render(state: PlaylistCardState) {
        when (state) {
            is PlaylistCardState.Content -> {
                showContent(state.playlist, state.tracks)
            }

            else -> findNavController().popBackStack()
        }
    }

    private fun showContent(playlist: Playlist, tracks: List<Track>) {
        _binding?.apply {
            name.text = playlist.name
            description.text = playlist.description

            Glide.with(requireContext())
                .load(playlist.image)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_360)
                .into(image)

            val traсksDescr =
                when (playlist.count) {
                    1 -> "трек"
                    in 2..4 -> "трека"
                    in 5..9, 0 -> "треков"
                    else -> ""
                }
            count.text = "${playlist.count.toString()} $traсksDescr"

            val traсksTime = SimpleDateFormat(
                "mm",
                Locale.getDefault()
            ).format(tracks.sumOf { it.trackTimeMillis }).toInt()

            val tracksMin =
                when (traсksTime) {
                    1 -> "минута"
                    in 2..4 -> "минуты"
                    else -> "минут"
                }
            time.text = "${traсksTime.toString()} $tracksMin"

            if (tracks.isEmpty())
            {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.empty_playlist_mes),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (tracks.isEmpty()) {
            _binding?.tracksBottomSheet?.visibility = View.GONE
        }

        adapter?.tracks = tracks
        adapter?.notifyDataSetChanged()

        adapterPlaylist?.playlists = listOf(playlist)
        adapterPlaylist?.notifyDataSetChanged()
    }
    private fun sharePlaylist () {
        if (adapter!!.tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.empty_playlist_card),
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            viewModel.sharePlaylist()
        }
    }
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        private const val ARGS_PLAYLIST_ID = "PLAYLIST_EXTRA"
        fun createArgs(playlistId: Int): Bundle = bundleOf(ARGS_PLAYLIST_ID to playlistId)

    }
}