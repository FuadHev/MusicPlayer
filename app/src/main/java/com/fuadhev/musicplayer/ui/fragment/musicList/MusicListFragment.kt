package com.fuadhev.musicplayer.ui.fragment.musicList

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fuadhev.musicplayer.R
import com.fuadhev.musicplayer.databinding.FragmentMusicListBinding
import com.fuadhev.musicplayer.entity.Music
import com.fuadhev.musicplayer.service.MusicPlayerService
import com.fuadhev.musicplayer.ui.adapter.MusicAdapter
import com.fuadhev.musicplayer.ui.adapter.MusicClickListener
import com.fuadhev.musicplayer.utils.CurrentMusic
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.log
import kotlin.random.Random


class MusicListFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    //    Glide.with(this)
//    .load(R.drawable.your_drawable_resource) // Drawable kaynağını belirtin
//    .transition(DrawableTransitionOptions.withCrossFade())
//    .into(imageView)
    private val RC_STORAGE_PERMISSION = 123
    private val RC_NOTIFICATION_PERMISSION = 456
    private lateinit var binding: FragmentMusicListBinding
    private val musicAdapter by lazy {
        MusicAdapter(this,object : MusicClickListener {
            override fun musicClickListener(bundle: Bundle) {
                findNavController().navigate(R.id.action_musicListFragment_to_musicFragment,bundle)
            }

        }, emptyList())
    }
    private val musicBackgrounds = arrayOf("b5", "b2", "b4", "b3", "b1", "b6")
    private lateinit var musicList: ArrayList<Music>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_list, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicList = ArrayList()
        setRecyclerview()
        checkPermission()




    }

    private fun setRecyclerview() {
        binding.musicRv.layoutManager = LinearLayoutManager(requireContext())
        binding.musicRv.adapter = musicAdapter
    }

    @SuppressLint("Range")
    private fun loadMusic() {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )

        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DATA} NOT LIKE '%WhatsApp/Media/WhatsApp Audio%'"

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val duration =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                val randomIndex = Random.nextInt(musicBackgrounds.size)
                val img = musicBackgrounds[randomIndex]


                val music = Music(title, artist, album, data, img, duration)
                musicList.add(music)

            }
            musicAdapter.updateList(musicList)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (EasyPermissions.hasPermissions(
                    requireActivity(),
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
            ) {
                loadMusic()
                // İzin zaten alındı, devam etmek için gerekli işlemleri yapabilirsiniz
            } else {
//            // İzin alınmamış, iste
                EasyPermissions.requestPermissions(
                    requireActivity(),
                    "The app requires external storage permission to run.",
                    RC_STORAGE_PERMISSION,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_MEDIA_AUDIO,

                )
            }
        }else{
            if (EasyPermissions.hasPermissions(
                    requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
            ) {
                loadMusic()
                // İzin zaten alındı, devam etmek için gerekli işlemleri yapabilirsiniz
            } else {
//            // İzin alınmamış, iste
                EasyPermissions.requestPermissions(
                    requireActivity(),
                    "The app requires external storage permission to run.",
                    RC_STORAGE_PERMISSION,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_MEDIA_AUDIO,

//                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
            }
        }


        // Bildirim iznini kontrol et ve gerekirse iste
        if (EasyPermissions.hasPermissions(
                requireActivity(),
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY

            )
        ) {
            Log.e("notification izni", "checkPermission: notification ", )
            // İzin zaten alındı, devam etmek için gerekli işlemleri yapabilirsiniz
        } else {
            // İzin alınmamış, iste
            EasyPermissions.requestPermissions(
                requireActivity(),
                "The app needs notification permission to control notifications.",
                1,
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // İzin sonuçlarını EasyPermissions'a iletin



        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // İzinler verildiğinde yapılacak işlemleri buraya ekleyebilirsiniz
        loadMusic()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // İzin reddedildiğinde yapılacak işlemleri buraya ekleyebilirsiniz
        // Eğer kullanıcı "izinleri tekrar sorma"yı seçerse, AppSettingsDialog'ı başlatın
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

}