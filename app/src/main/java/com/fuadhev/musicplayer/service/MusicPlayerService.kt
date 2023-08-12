package com.fuadhev.musicplayer.service

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.fuadhev.musicplayer.R
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.repo.Repository
import com.fuadhev.musicplayer.root.MainActivity
import com.fuadhev.musicplayer.utils.CurrentMusic
import com.fuadhev.musicplayer.utils.CurrentMusic.currentMusicLiveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerService : Service() {

    private val TAG = "MusicService"
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var songs: ArrayList<Music>
    var songIndex: Int = 0
    private var currentMusic: String = ""
    private lateinit var mediaSession: MediaSessionCompat
    val musicIsplaying=MutableLiveData<Boolean>()

    @Inject
    lateinit var repo:Repository

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaSession = MediaSessionCompat(this, "Player Audio")
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called")

        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                //            val songPath = intent.getStringExtra("song_path")
                if (isMusicPlaying()) {
                    // Eğer müzik çalıyorsa, duraklat
                    pauseSong()
                } else {
                    // Eğer müzik duraksı ise, en son bilinen konumdan devam et
                    playSong(songs[songIndex].path)
//                    currentMusicLiveData.postValue(songs[songIndex])//
                }
            }
            ACTION_NEXT -> {
                skipToNextSong()
            }
            ACTION_PREVIOUS -> {
                skipToPreviousSong()
            }
        }



        return START_NOT_STICKY
    }

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    private val binder = MusicPlayerBinder()


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBind(intent: Intent): IBinder {
        songs = ArrayList()
        songIndex = intent.getIntExtra("song_index", 0)
        songs = intent.getParcelableArrayListExtra<Music>("musicList") as ArrayList<Music>

        mediaPlayer.setOnCompletionListener {
            skipToNextSong()
        }


        playSong(songs[songIndex].path)
//        currentMusicLiveData.postValue(songs[songIndex])
        return binder
    }

    override fun onDestroy() {
        // Release the MediaPlayer object
        mediaPlayer.release()
    }
    // Bu özel seekTo yöntemini ekleyin


    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.S)
    fun playSong(songPath: String?) {
//        if (songPath.isNullOrEmpty()) return
        try {
            musicIsplaying.postValue(true)
            if (mediaPlayer.currentPosition == 0 || songPath != currentMusic) {

                mediaPlayer.stop()
                mediaPlayer.reset()

                // Set the song path to the MediaPlayer object
                mediaPlayer.setDataSource(songPath)

                // Prepare the MediaPlayer object
                mediaPlayer.prepare()
                currentMusic = songPath!!
                val music = songs[songIndex]
//   /storage/emulated/0/Sounds/20230118_213636.m4a

                Log.e("songpath", "playSong: $songPath")
                val context: Context = applicationContext
                val resourceId =
                    resources.getIdentifier(music.m_img, "drawable", context.packageName)
                val backgroundBitmap = BitmapFactory.decodeResource(resources, resourceId)
                showNotification(music.m_name, music.artist_name, backgroundBitmap)
                currentMusicLiveData.postValue(songs[songIndex])
                CurrentMusic.currentMusic.postValue(songPath)
                // Start playing the song
                music.lastPlayTime=System.currentTimeMillis()

                CoroutineScope(IO).launch {
                    repo.insertMusic(music)
                }

                mediaPlayer.start()
            } else {
                mediaPlayer.start()
            }
            // Stop any currently playing song

        } catch (e: Exception) {
            Log.e(TAG, "Error playing song: ${e.message}")
        }
    }

    fun isMusicPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

//    fun seekTo(progress: Int) {
//        mediaPlayer.seekTo(progress)
//    }
    // Bu özel seekTo yöntemini ekleyin

    fun pauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            musicIsplaying.postValue(false)
        }
    }

    fun stopSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun skipToNextSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        songIndex = (songIndex + 1) % songs.size
        val nextSongPath = songs[songIndex].path
//        currentMusicLiveData.postValue(songs[songIndex])
        CurrentMusic.currentMusic.postValue(nextSongPath)
        currentMusicLiveData.postValue(songs[songIndex])

        playSong(nextSongPath)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun skipToPreviousSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        songIndex = (songIndex - 1) % songs.size
        if (songIndex < 0) {
            songIndex = songs.size - 1
        }
        val previousSongPath = songs[songIndex].path
//        currentMusicLiveData.postValue(songs[songIndex])
        CurrentMusic.currentMusic.postValue(previousSongPath)
        currentMusicLiveData.postValue(songs[songIndex])


        playSong(previousSongPath)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "MusicPlayerChannelId"
            val channelName = "Music Player"
            val channelDescription = "Music Player Channel"

            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = channelDescription
                enableLights(true)
                lightColor = Color.BLUE
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showNotification(
        music_name: String,
        artist_name: String,
        backgroundBitmap: Bitmap?
    ) {
        val channelId = "MusicPlayerChannelId"

        // Bildirim tıklandığında açılacak aktiviteyi ayarlayalım
        val pendingIntent = Intent(this, MainActivity::class.java).let { intent ->
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(backgroundBitmap)
            .bigLargeIcon(backgroundBitmap) // Büyük ikon olarak backgroundBitmap'i kullan


        // Bildirimde kullanılacak düğmelerin işlemleri için Intent oluşturalım
        val playPauseIntent = Intent(this, MusicPlayerService::class.java).apply {
            action = ACTION_PLAY_PAUSE

        }
        val previousIntent = Intent(this, MusicPlayerService::class.java).apply {
            action = ACTION_PREVIOUS

        }
        val nextIntent = Intent(this, MusicPlayerService::class.java).apply {
            action = ACTION_NEXT

        }

        val playPausePendingIntent: PendingIntent = PendingIntent.getService(
            this,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val previousPendingIntent: PendingIntent = PendingIntent.getService(
            this,
            0,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val nextPendingIntent: PendingIntent = PendingIntent.getService(
            this,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // Bildirim oluşturma
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(music_name)
            .setContentText(artist_name)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.skip_previous, "Previous", previousPendingIntent)
            .addAction(R.drawable.play, "Play/Pause", playPausePendingIntent)
            .addAction(R.drawable.skip_next, "Next", nextPendingIntent)
            .setStyle(bigPictureStyle)
            .build()

        startForeground(1, notification)
    }
//    fun getCurrentPosition(): Int {
//        return mediaPlayer.currentPosition
//    }

    companion object {
//        const val ACTION_PLAY = "com.fuadhev.musicplayer.action.PLAY"
        const val ACTION_PLAY_PAUSE = "com.fuadhev.musicplayer.action.PLAY_PAUSE"
        const val ACTION_PREVIOUS = "com.fuadhev.musicplayer.action.PREVIOUS"
        const val ACTION_NEXT = "com.fuadhev.musicplayer.action.NEXT"
    }
}

class MusicPlayerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        when (intent.action) {
            MusicPlayerService.ACTION_PLAY_PAUSE -> {
                // Play/Pause eylemi burada işlenecek
                val serviceIntent = Intent(context, MusicPlayerService::class.java)
                serviceIntent.action = intent.action
                context.startService(serviceIntent)
            }
            MusicPlayerService.ACTION_PREVIOUS -> {
                // Previous eylemi burada işlenecek
                val serviceIntent = Intent(context, MusicPlayerService::class.java)
                serviceIntent.action = intent.action
                context.startService(serviceIntent)
            }
            MusicPlayerService.ACTION_NEXT -> {
                // Next eylemi burada işlenecek
                val serviceIntent = Intent(context, MusicPlayerService::class.java)
                serviceIntent.action = intent.action
                context.startService(serviceIntent)
            }
        }
    }
}
