package com.example.tuneit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tuneit.R
import com.example.tuneit.data.Song

class SongAdapter(val songList: List<Song>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songName.text = songList[position].Name
        holder.songArtist.text = songList[position].Artist
    }

    override fun getItemCount(): Int = songList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.song_name)
        val songArtist: TextView = view.findViewById(R.id.song_artist)

    }
}