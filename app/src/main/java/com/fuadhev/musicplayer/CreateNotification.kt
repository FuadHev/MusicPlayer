package com.fuadhev.musicplayer

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fuadhev.musicplayer.entity.Music

class CreateNotification {
    val CHANNEL_ID = "channel1"
    val ACTIONPREVIUOS = "actionpreviuos"
    val CHANNEL_PLAY = "actionplay"
    val CHANNEL_NEXT = "actionnext"
    lateinit var notification: Notification
//
//    fun createNotification(context: Context, music: Music, playButton: Int, pos: Int, size: Int) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationManager = NotificationManagerCompat.from(context)
//            val mediaSessionCompat = MediaSessionCompat(context, "tag")
//            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle()
//        }
//
//
//    }

}