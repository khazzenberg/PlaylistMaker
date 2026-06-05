package com.practicum.playlistmaker.favoritetracks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.favoritetracks.presentation.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.root.ui.RootActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.models.TracksState
import com.practicum.playlistmaker.util.debounce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    private val gson: Gson by inject()
    private var _binding: FragmentFavoriteTracksBinding? = null

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private lateinit var onTrackSearchDebounce: (Track) -> Unit
    private val tracks: MutableList<Track> = mutableListOf()
    private val adapter: TrackAdapter = TrackAdapter(tracks)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.onTrackClick = { track ->
            (activity as RootActivity).animateBottomNavigationView()
            onTrackSearchDebounce(track)
        }

        _binding?.trackList?.adapter = adapter
        _binding?.trackList?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackSearchDebounce =
            debounce<Track>(
                CLICK_DEBOUNCE_DELAY,
                viewLifecycleOwner.lifecycleScope,
                false
            ) { track ->
                findNavController().navigate(
                    R.id.action_libraryFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.Companion.createArgs(gson.toJson(track))
                )
            }
    }

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showEmpty()
            else -> {}
        }
    }

    fun showEmpty() {
        _binding?.apply {
            emptyLibrary.visibility = View.VISIBLE
            trackList.visibility = View.GONE
        }
    }

    fun showContent(foundTrack: List<Track>) {
        _binding?.apply {
            emptyLibrary.visibility = View.GONE
            trackList.visibility = View.VISIBLE
            tracks.clear()
            tracks.addAll(foundTrack)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoriteTracksFragment()
    }
}