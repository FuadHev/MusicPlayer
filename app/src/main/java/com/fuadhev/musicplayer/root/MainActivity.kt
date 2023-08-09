package com.fuadhev.musicplayer.root

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.fuadhev.musicplayer.R
import com.fuadhev.musicplayer.service.MusicPlayerService
import com.fuadhev.musicplayer.utils.CurrentMusic

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
    override fun onDestroy() {
        super.onDestroy()
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        stopService(serviceIntent)
    }

}