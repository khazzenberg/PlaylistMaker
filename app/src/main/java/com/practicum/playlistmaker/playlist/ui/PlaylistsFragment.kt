package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.playlist.domain.model.Playlist
import com.practicum.playlistmaker.playlist.presentation.PlaylistState
import com.practicum.playlistmaker.playlist.presentation.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val viewModel: PlaylistViewModel by viewModel()
    private var adapter: PlaylistAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        _binding?.newPlaylist?.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_createPlaylistFragment
            )
        }

        adapter = PlaylistAdapter { playlistId ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playlistCardFragment,
                PlaylistCardFragment.createArgs(playlistId)
            )
        }
        _binding?.recyclerView?.adapter = adapter
        _binding?.recyclerView?.layoutManager = GridLayoutManager(
            requireContext(),
            2
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding?.recyclerView?.adapter = null
    }

    fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty()
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        _binding?.newPlaylist?.visibility = View.VISIBLE
        _binding?.emptyLibrary?.visibility = View.GONE
        _binding?.recyclerView?.visibility = View.VISIBLE
        adapter?.playlists = playlists
        adapter?.notifyDataSetChanged()
    }

    private fun showEmpty() {
        _binding?.newPlaylist?.visibility = View.VISIBLE
        _binding?.emptyLibrary?.visibility = View.VISIBLE
        _binding?.recyclerView?.visibility = View.GONE
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}