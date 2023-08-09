package com.fuadhev.musicplayer.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fuadhev.musicplayer.R
import com.fuadhev.musicplayer.databinding.MusicItemBinding
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.utils.CurrentMusic

class MusicAdapter(val owner:LifecycleOwner,private val musicClickListener: MusicClickListener,private var musicList:List<Music>):RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

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
        musicList= newmusicList
        notifyDataSetChanged()
    }

    @SuppressLint("DiscouragedApi", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music=musicList[position]
        val b=holder.view
        val context=holder.itemView.context
//        val context=holder.itemView.context
        val resourceId = context.resources.getIdentifier(music.m_img, "drawable", context.packageName)
        b.musicImg.setImageResource(resourceId)

        b.musicName.text=music.m_name
        CurrentMusic.currentMusic.observe(owner){
            if (it!=null){
                if (it==music.path){
                    b.musicItem.setBackgroundResource(R.drawable.isplay)
                    b.lottie.visibility= VISIBLE
                    b.lottie.playAnimation()
                    b.musicName.requestFocus()

                }else{
                    b.musicItem.setBackgroundResource(R.drawable.noplay_bg)
                    b.lottie.visibility=GONE
                    b.lottie.cancelAnimation()

                }
            }

        }

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