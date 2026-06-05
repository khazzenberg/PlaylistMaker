package com.practicum.playlistmaker.playlist.ui

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackInPlaylistViewHolder(private val binding: TrackViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Track) {
        binding.trackName.text = model.trackName
        binding.artistName.text = ""
        binding.artistName.text = model.artistName
        binding.trackTime.text = model.getFormattedTime()
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(binding.imageAlbum)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    companion object {
        fun from(parent: ViewGroup): TrackInPlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackViewBinding.inflate(inflater, parent, false)
            return TrackInPlaylistViewHolder(binding)
        }
    }
}