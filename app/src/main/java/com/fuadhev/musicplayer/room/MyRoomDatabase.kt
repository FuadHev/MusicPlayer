package com.fuadhev.musicplayer.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fuadhev.musicplayer.entity.Music

@Database(entities = [Music::class], version = 1)
abstract class MyRoomDatabase:RoomDatabase(){
    abstract fun musicDao():MusicDao
}