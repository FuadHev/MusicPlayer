package com.fuadhev.musicplayer.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fuadhev.musicplayer.databinding.MusicItemBinding
import com.fuadhev.musicplayer.entity.Music

class MusicAdapter(private var musicList:List<Music>):RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    inner class ViewHolder(val view:MusicItemBinding):RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MusicItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newmusicList: List<Music>){
        musicList=newmusicList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music=musicList[position]
        val b=holder.view
        b.musicName.text=music.m_name

    }

}