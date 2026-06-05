package com.practicum.playlistmaker.library.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.favoritetracks.ui.FavoriteTracksFragment
import com.practicum.playlistmaker.playlist.ui.PlaylistsFragment

class LibraryViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(
    fragmentManager,
    lifecycle
) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}