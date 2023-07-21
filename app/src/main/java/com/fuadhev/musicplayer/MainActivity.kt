package com.fuadhev.musicplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {
    private  val RC_STORAGE_PERMISSION = 123
    private  val RC_NOTIFICATION_PERMISSION = 456
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




//        if (EasyPermissions.hasPermissions(
//                this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_MEDIA_AUDIO
//            )
//        ) {
//            // İzin zaten alındı, devam etmek için gerekli işlemleri yapabilirsiniz
//        } else {
////            // İzin alınmamış, iste
//            EasyPermissions.requestPermissions(
//                this,
//                "Uygulamanın çalışması için dış depolama izni gerekiyor.",
//                RC_STORAGE_PERMISSION,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_MEDIA_AUDIO
//            )
//        }
//
//        // Bildirim iznini kontrol et ve gerekirse iste
//        if (EasyPermissions.hasPermissions(
//                this,
//                android.Manifest.permission.POST_NOTIFICATIONS
//            )
//        ) {
//            // İzin zaten alındı, devam etmek için gerekli işlemleri yapabilirsiniz
//        } else {
//            // İzin alınmamış, iste
//            EasyPermissions.requestPermissions(
//                this,
//                "Uygulamanın bildirimleri kontrol etmek için bildirim izni gerekiyor.",
//                RC_NOTIFICATION_PERMISSION,
//                android.Manifest.permission.POST_NOTIFICATIONS
//            )
//        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        // İzin sonuçlarını EasyPermissions'a iletin
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
//    }
//
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        // İzinler verildiğinde yapılacak işlemleri buraya ekleyebilirsiniz
//    }
//
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        // İzin reddedildiğinde yapılacak işlemleri buraya ekleyebilirsiniz
//        // Eğer kullanıcı "izinleri tekrar sorma"yı seçerse, AppSettingsDialog'ı başlatın
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            AppSettingsDialog.Builder(this).build().show()
//        }
//    }
}