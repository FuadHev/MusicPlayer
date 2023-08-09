package com.fuadhev.musicplayer.utils

import androidx.lifecycle.MutableLiveData
import com.fuadhev.musicplayer.entity.Music

object CurrentMusic {


   var currentMusic=MutableLiveData<String>()
   val currentMusicLiveData= MutableLiveData<Music>()
}