package com.fuadhev.musicplayer.ui.fragment.music

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.fuadhev.musicplayer.R
import com.fuadhev.musicplayer.databinding.FragmentMusicBinding
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.service.MusicPlayerService
import com.fuadhev.musicplayer.utils.CurrentMusic
import com.fuadhev.musicplayer.utils.CurrentMusic.currentMusicLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicFragment : Fragment() {

    private lateinit var binding: FragmentMusicBinding
    private lateinit var allmusicList: List<Music>
    private var musicService: MusicPlayerService? = null
    private var isMusicServiceBound = false
    private var position = 0
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

    @SuppressLint("DiscouragedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()

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
            binding.mName.text=allmusicList[position].m_name
            binding.artistName.text=allmusicList[position].artist_name
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)


    }

    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("music", "viewcreated: viewcreated")

        startAnimation()





//        Handler().postDelayed({
//            initialiseSeekbar()
//        }, 500)


//        initialiseSeekbar()
        binding.mName.requestFocus()
        binding.artistName.requestFocus()


        Handler(Looper.getMainLooper()).postDelayed({
            // Eğer müzik duraksı ise, en son bilinen konumdan devam et
            if(CurrentMusic.currentMusic.value!=allmusicList[position].path){
                musicService?.songIndex=position
                musicService?.playSong(allmusicList[position].path)
            }
            musicService?.musicIsplaying?.observe(viewLifecycleOwner){
                if(it){
                    binding.playPauseFab.setImageResource(R.drawable.pause)
                    startAnimation()
//                    binding.play.setImageResource(R.drawable.pause)
                }else{
                    binding.playPauseFab.setImageResource(R.drawable.play)
                    binding.musicImg.clearAnimation()
//                    binding.play.setImageResource(R.drawable.play)

                }
            }
            initialiseSeekbar()

        }, 500)



        currentMusicLiveData.observe(viewLifecycleOwner){
            val music=it
            binding.mName.text=music.m_name
            binding.artistName.text=music.artist_name

            val resourceId =
                resources.getIdentifier(it.m_img, "drawable", requireActivity().packageName)
           binding.musicImg.setImageResource(resourceId)
        }

        binding.playPauseFab.setOnClickListener {

            if (isMusicServiceBound && musicService != null) {

                if (musicService?.isMusicPlaying() == true) {
                    // Eğer müzik çalıyorsa, duraklat
                    musicService?.pauseSong()
                    binding.musicImg.clearAnimation()
                } else {
                    startAnimation()
//                    initialiseSeekbar()
                    // Eğer müzik duraksı ise, en son bilinen konumdan devam et
                    musicService?.playSong(allmusicList[position].path)

                }
            }

        }


        binding.skipNextFab.setOnClickListener {
            musicService?.skipToNextSong()

        }
        binding.skipPreviousFab.setOnClickListener {
            musicService?.skipToPreviousSong()

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

    private fun startAnimation() {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
            duration = 3000
        }
        binding.musicImg.startAnimation(rotateAnimation)
    }

    private fun initialiseSeekbar() {
        val mp = musicService?.mediaPlayer
        binding.slider.max = musicService?.mediaPlayer?.duration?:23000

        Log.e("duration", "initialiseSeekbar: ${musicService?.mediaPlayer?.duration}", )
        Log.e("slidermax", "initialiseSeekbar: ${binding.slider.max}", )
        Log.e("durationservis", "initialiseSeekbar: ${musicService}", )
        Log.e("durationplayer", "initialiseSeekbar: ${musicService?.mediaPlayer}", )
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    binding.slider.progress = mp!!.currentPosition

                    val currentdurationInMillis = mp.currentPosition
                    val minutes = (currentdurationInMillis / 1000) / 60
                    val seconds = (currentdurationInMillis / 1000) % 60
                    val formattedCurrentDuration = String.format("%02d:%02d", minutes, seconds)

                    val durationInMillis = mp.duration
                    val dminutes = (durationInMillis / 1000) / 60
                    val dseconds = (durationInMillis / 1000) % 60
                    val formattedDuration = String.format("%02d:%02d", dminutes, dseconds)

                    // TextView içinde süreyi gösterin
                    binding.currentTime.text = formattedCurrentDuration
                    binding.duration.text = formattedDuration
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