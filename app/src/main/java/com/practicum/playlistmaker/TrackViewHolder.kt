package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(parent : ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.track_view, parent, false)
) {
    private val sourceImage: ImageView = itemView.findViewById(R.id.imageAlbum)
    private val sourceName: TextView = itemView.findViewById(R.id.trackName)
    private val sourceTime: TextView = itemView.findViewById(R.id.trackTime)
    private val sourceArtist: TextView = itemView.findViewById(R.id.artistName)

    fun bind(track: Track) {
        sourceName.text = track.trackName
        sourceArtist.text = track.artistName
        sourceTime.text = track.getFormattedTime()
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(sourceImage)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}