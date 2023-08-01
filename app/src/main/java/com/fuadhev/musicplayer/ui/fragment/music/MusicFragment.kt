package com.fuadhev.musicplayer.ui.fragment.music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.fuadhev.musicplayer.R
import com.fuadhev.musicplayer.databinding.FragmentMusicBinding
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.service.MusicPlayerService
import com.fuadhev.musicplayer.utils.CurrentMusic
import jp.wasabeef.blurry.Blurry


class MusicFragment : Fragment() {

    private lateinit var binding: FragmentMusicBinding
    private lateinit var allmusicList: List<Music>
    private var musicService: MusicPlayerService? = null
    private var isMusicServiceBound = false
    private var position = 0
    private var currentDuration: Int? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicService = binder.getService()
            isMusicServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isMusicServiceBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music, container, false)
        // Inflate the layout for this fragment
//        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.b1)
//
//// ImageView'ın içeriğini blur yapın ve ImageView'a yükleyin
//        Blurry.with(requireContext())
//            .radius(25)
//            .sampling(10)
//            .from(originalBitmap)
//            .into(binding.img)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.e("music", "onStart: musiconstart")
        if (musicService == null) {
            val bundle = arguments
            // duzgun yol olmadigini bilirem sadece asand olsun deye etmisem
            allmusicList = bundle?.getParcelableArrayList("musics") ?: emptyList()
            position = bundle?.getInt("position") ?: 0

            val intent = Intent(requireContext(), MusicPlayerService::class.java)
            intent.putExtra("song_index", position)
            intent.putParcelableArrayListExtra(
                "musicList",
                allmusicList as java.util.ArrayList<out Parcelable>
            )
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Handler().postDelayed({
            initialiseSeekbar()

        }, 500)


        Handler().postDelayed({
            initialiseSeekbar()
            // Eğer müzik duraksı ise, en son bilinen konumdan devam et
            musicService?.playSong(allmusicList[position].path)
        }, 500)




        binding.play.setOnClickListener {

            if (isMusicServiceBound && musicService != null) {
                initialiseSeekbar()
                if (musicService?.isMusicPlaying() == true) {
                    // Eğer müzik çalıyorsa, duraklat
                    musicService?.pauseSong()
                } else {

                    // Eğer müzik duraksı ise, en son bilinen konumdan devam et
                    musicService?.playSong(allmusicList[position].path)
                }
            }
        }

        binding.skipNext.setOnClickListener {
            musicService?.skipToNextSong()
        }
        binding.skipPrevious.setOnClickListener {
            musicService?.skipToPreviousSong()
        }

        binding.pause.setOnClickListener {
            musicService?.pauseSong()
        }


        binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService?.mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })


    }

    private fun initialiseSeekbar() {
        val mp = musicService?.mediaPlayer
        binding.slider.max = musicService?.mediaPlayer?.duration!!
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    binding.slider.progress = mp!!.currentPosition
                    handler.postDelayed(this, 1000)
                } catch (e: java.lang.Exception) {
                    binding.slider.progress = 0
                }
            }
        }, 0)
    }

//    override fun onStart() {
//        super.onStart()
//        val intent = Intent(requireContext(), MusicPlayerService::class.java)
//        intent.putExtra("song_index", position)
//        intent.putParcelableArrayListExtra(
//            "musicList",
//            allmusicList as java.util.ArrayList<out Parcelable>
//        )
//        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
////        val intent = Intent(requireContext(), MusicPlayerService::class.java)
////        intent.putExtra("song_path", allmusicList[position])
////        intent.putParcelableArrayListExtra("musicList",allmusicList as java.util.ArrayList<out Parcelable>)
////        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
//    }

//    private fun playSong(songPath: String, songIndex: Int) {
//        val intent = Intent(requireContext(), MusicPlayerService::class.java)
//        intent.action = MusicPlayerService.ACTION_PLAY
//        intent.putExtra("song_path", songPath)
//        intent.putExtra("song_index", songIndex)
//        intent.putParcelableArrayListExtra("musicList",allmusicList as java.util.ArrayList<out Parcelable>)
//        requireContext().startService(intent)
//    }


}