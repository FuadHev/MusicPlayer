package com.fuadhev.musicplayer.ui.fragment.musicList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(private val repo:Repository) :ViewModel() {

    lateinit var lastPlayedMusics:LiveData<List<Music>>

    fun getLastPlayedMusic(){
        viewModelScope.launch(IO) {
            lastPlayedMusics= repo.getLastPlayedMusics()
        }
    }

    fun insertMusic(music: Music){
        viewModelScope.launch(IO) {
            repo.insertMusic(music)
        }
    }

}