package com.fuadhev.musicplayer.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fuadhev.musicplayer.entity.Music


@Dao
interface MusicDao {

    @Query("SELECT * FROM Music")
    fun getLastPlayMusics(): LiveData<List<Music>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertMusic(music: Music)
}