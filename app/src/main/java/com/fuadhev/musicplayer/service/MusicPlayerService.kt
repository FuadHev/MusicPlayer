package com.fuadhev.musicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.fuadhev.musicplayer.entity.Music

class MusicPlayerService : Service() {

    private val TAG = "MusicService"
     lateinit var mediaPlayer: MediaPlayer
    private lateinit var songs: ArrayList<Music>
    private var songIndex: Int = 0

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called")

        if (intent?.action == ACTION_PLAY) {
//            val songPath = intent.getStringExtra("song_path")

//            // Play the selected song
//            playSong(songPath)
        }



        return START_NOT_STICKY
    }

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    private val binder = MusicPlayerBinder()


    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind() called")
        songs=ArrayList()
        songIndex = intent.getIntExtra("song_index", 0)
        songs= intent.getParcelableArrayListExtra<Music>("musicList") as ArrayList<Music>

        mediaPlayer.setOnCompletionListener {
            skipToNextSong()
        }

        playSong(songs[songIndex].path)
        return binder
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        // Release the MediaPlayer object
        mediaPlayer.release()
    }
    // Bu özel seekTo yöntemini ekleyin


    fun playSong(songPath: String?) {
//        if (songPath.isNullOrEmpty()) return

        try {
            if (mediaPlayer.currentPosition==0){
                Log.e("TAG", "playSong: ${mediaPlayer.currentPosition}", )
                mediaPlayer.stop()
                mediaPlayer.reset()

                // Set the song path to the MediaPlayer object
                mediaPlayer.setDataSource(songPath)

                // Prepare the MediaPlayer object
                mediaPlayer.prepare()

                // Start playing the song
                mediaPlayer.start()
            }else{
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
    fun seekTo(progress: Int) {
        mediaPlayer.seekTo(progress)
    }
    // Bu özel seekTo yöntemini ekleyin

    fun pauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun stopSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    fun skipToNextSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        songIndex = (songIndex + 1) % songs.size
        val nextSongPath = songs[songIndex].path
        playSong(nextSongPath)
    }

    fun skipToPreviousSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        songIndex = (songIndex - 1) % songs.size
        if (songIndex<0){
            songIndex=songs.size-1
        }
        val previousSongPath = songs[songIndex].path
        playSong(previousSongPath)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    companion object {
        const val ACTION_PLAY = "com.fuadhev.musicplayer.action.PLAY"
    }
}
