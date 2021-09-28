package com.example.wallpaperapp.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.example.wallpaperapp.util.ext.lifecycleOwner
import com.example.wallpaperapp.util.ext.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PermissionUtil(private val activity: FragmentActivity, private val context: Context) {


    private val permissions = arrayOf(
        Manifest.permission.SET_WALLPAPER,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestResult = SingleLiveEvent<Boolean>()
    private var declineCounter = 0

    fun checkPermissions(callback: (Boolean) -> Unit) {
        Log.e("sss", "check permission")
        var counter = 0
        for (element in permissions) {
            ActivityCompat.checkSelfPermission(
                context,
                element
            ).let {
                if (it == PackageManager.PERMISSION_GRANTED) {
                    counter++
                }

            }
        }
        if (counter == permissions.size)
            callback(true)
        else {
            requestPermissions.launch(permissions)
            context.lifecycleOwner()?.let { owner ->
                requestResult.observe(owner) {
                    requestResult.removeObservers(owner)
                }
            }
        }
    }


    private val requestPermissions =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            Log.e("sss", "request permissions")
            if (result.none { it.value == false }) {
                Log.e("sss", "true")

                requestResult.postValue(true)
            } else {
                declineCounter++
                if (declineCounter == 2) {
                    showPermissionDialog()
                    declineCounter = 0
                }
                activity.showToast("Uygulamanın kullanılabilmsesi için lütfen gerekli izinleri verin.")
                requestResult.postValue(false)
            }

        }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(context).setTitle("İzin gerekli")
            .setMessage("Resimleri indirmek ve duvar kağıdı olarak ayarlamak için açılan sayfada gerekli izinleri verin.")
            .setPositiveButton("Tamam") { _, _ ->

                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }.setNegativeButton("İptal") { _, _ ->

            }.show()
    }
}