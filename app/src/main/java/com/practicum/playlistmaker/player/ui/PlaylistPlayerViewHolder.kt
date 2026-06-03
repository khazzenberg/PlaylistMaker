package com.practicum.playlistmaker.player.ui


import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.domain.model.Playlist

class PlaylistPlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = itemView.findViewById(R.id.namePlaylist)
    private val count: TextView = itemView.findViewById(R.id.countTracks)
    private val image: ImageView = itemView.findViewById(R.id.imagePlaylist)
    fun bind(model: Playlist) {
        name.text = model.name
        count.text = model.count.toString()
        val traсks =
            when (model.count) {
                1 -> "трек"
                in 2..4 -> "трека"
                in 5..9, 0 -> "треков"
                else -> ""
            }
        count.text = "${model.count.toString()} $traсks"
        Glide.with(itemView)
            .load(model.image)
            .placeholder(R.drawable.ic_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(image)
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}