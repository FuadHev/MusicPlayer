package com.fuadhev.musicplayer.ui.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.fuadhev.musicplayer.R
import com.fuadhev.musicplayer.databinding.MusicItemBinding
import com.fuadhev.musicplayer.databinding.MusicLpItemBinding
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.utils.CurrentMusic

class MusicLPAdapter(private val musicClickListener: MusicLPClickListener, private var musicList:List<Music>):
    RecyclerView.Adapter<MusicLPAdapter.ViewHolder>() {

    inner class ViewHolder(val view: MusicLpItemBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MusicLpItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newmusicList: List<Music>){
        musicList= newmusicList
        notifyDataSetChanged()
    }

    @SuppressLint("DiscouragedApi", "ResourceAsColor", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music=musicList[position]
        val b=holder.view
        val context=holder.itemView.context
//        val context=holder.itemView.context
        val resourceId = context.resources.getIdentifier(music.m_img, "drawable", context.packageName)
        b.musicImg.setImageResource(resourceId)

        b.musicName.text=music.m_name

        b.musicItem.setOnClickListener {
            musicClickListener.musicClickListener(music.path)
            b.musicName.requestFocus()
        }


    }

}
interface MusicLPClickListener{
    fun musicClickListener(path:String)

}