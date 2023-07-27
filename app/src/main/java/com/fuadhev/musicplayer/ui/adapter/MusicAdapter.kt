package com.fuadhev.musicplayer.ui.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fuadhev.musicplayer.databinding.MusicItemBinding
import com.fuadhev.musicplayer.entity.Music

class MusicAdapter(private val musicClickListener: MusicClickListener,private var musicList:List<Music>):RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

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

        b.musicItem.setOnClickListener {
            val bundle= Bundle()
            bundle.putParcelableArrayList("musics",musicList as java.util.ArrayList<out Parcelable>)
            bundle.putInt("position",position)
            musicClickListener.musicClickListener(bundle)
        }


    }

}
interface MusicClickListener{
    fun musicClickListener(bundle:Bundle)
}