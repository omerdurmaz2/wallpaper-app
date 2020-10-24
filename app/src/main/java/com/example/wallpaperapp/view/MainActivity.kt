package com.example.wallpaperapp.view

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.util.NavigationHelper
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var selectedImage: WeakReference<String>
        lateinit var selectedCategory: WeakReference<String>
        lateinit var isLocal: WeakReference<Boolean>
        lateinit var permission: WeakReference<Permission>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fixAllIcons()
        selectTab(0)
        permission = WeakReference(Permission(this))

    }

    private fun fixAllIcons() {
        val menuView =
            findViewById<BottomNavigationView>(R.id.bottomNavigationView).getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val ivParent = menuView.getChildAt(i)
            ivParent.setOnClickListener { selectTab(i) }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private fun selectTab(id: Int) {
        when (id) {
            0 -> {
                NavigationHelper.getInstance().toHome(supportFragmentManager)
            }
            1 -> {
                NavigationHelper.getInstance().toSearch(supportFragmentManager)
            }
            2 -> {
                NavigationHelper.getInstance().toProfile(supportFragmentManager)
            }
        }
    }
}