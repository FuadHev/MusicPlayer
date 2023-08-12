package com.fuadhev.musicplayer.repo

import androidx.lifecycle.LiveData
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.room.MusicDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class Repository @Inject constructor(private val musicDao:MusicDao) {

    fun getLastPlayedMusics(): LiveData<List<Music>> {
        return musicDao.getLastPlayMusics()
    }

    fun insertMusic(music: Music){
            musicDao.insertMusic(music)
    }

}