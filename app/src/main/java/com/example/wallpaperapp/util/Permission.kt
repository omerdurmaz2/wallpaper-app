package com.example.wallpaperapp.util

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.wallpaperapp.view.MainActivity

class Permission(val activity: MainActivity) {
    val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.SET_WALLPAPER
    )
    val REQUEST_CODE = 1000

    init {
        checkPermission()
    }


    fun checkPermission() {
        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    activity.applicationContext,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity,
            permissions, REQUEST_CODE
        )
    }
}