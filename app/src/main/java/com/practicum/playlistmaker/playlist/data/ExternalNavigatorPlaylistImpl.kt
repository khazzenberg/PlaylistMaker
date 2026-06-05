package com.practicum.playlistmaker.playlist.data

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import androidx.core.net.toUri
import com.practicum.playlistmaker.playlist.data.storage.ExternalNavigatorPlaylist

class ExternalNavigatorPlaylistImpl(
    private val context: Context
): ExternalNavigatorPlaylist {
    override fun sharePlaylist(getShareAppLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getShareAppLink)
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_with))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    }
}