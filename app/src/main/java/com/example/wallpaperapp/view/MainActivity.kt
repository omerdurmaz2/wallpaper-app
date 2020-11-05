package com.example.wallpaperapp.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.wallpaperapp.R
import com.example.wallpaperapp.dialog.LanguageDialog
import com.example.wallpaperapp.dialog.LoadingDialog
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.util.Permission
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var selectedImage: WeakReference<String>
        lateinit var selectedCategory: WeakReference<String>
        lateinit var isLocal: WeakReference<Boolean>
        lateinit var permission: WeakReference<Permission>
        lateinit var fragmentSavedState: HashMap<String, Fragment.SavedState?>
        lateinit var loadingDialog: LoadingDialog
        lateinit var languageDialog: LanguageDialog
        lateinit var languageButton: ImageButton
        private val FRAGMENT_STATE = "fragmentState"
        var searchText = ""

        var homeLoaded = false
        var libraryLoaded = false
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        languageDialog()

        if (savedInstanceState == null) {
            fragmentSavedState = HashMap()
            selectTab(0)
            fixAllIcons()
        } else {
            fragmentSavedState =
                savedInstanceState.getSerializable(FRAGMENT_STATE) as HashMap<String, Fragment.SavedState?>
        }
        loadingDialog = LoadingDialog(this)
        loadingDialog.isCancelable = false
        permission = WeakReference(Permission(this))
        loadLanguage()

    }

    private fun fixAllIcons() {
        val menuView =
            findViewById<BottomNavigationView>(R.id.bottomNavigationView).getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val ivParent = menuView.getChildAt(i)
            ivParent.setOnClickListener { selectTab(i) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun loadLanguage() {
        val pref: SharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lang = pref.getString("lang", "tr")
        setLanguage(lang)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun languageDialog() {
        languageButton = findViewById(R.id.btnLangueage)
        languageButton.setOnClickListener {
            languageDialog.show(supportFragmentManager, "LanguageDialog")
        }
        languageDialog = LanguageDialog(this) {
            languageDialog.dismiss()
            setLanguage(it)
        }

    }



    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setLanguage(lang: String? = "tr") {
        val configuration = Configuration()
        Locale.setDefault(Locale(lang))
        configuration.setLocale(Locale(lang))
        baseContext.resources.updateConfiguration(
            configuration,
            baseContext.resources.displayMetrics
        )

        val editor: SharedPreferences.Editor =
            getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("lang", lang)
        editor.apply()
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(FRAGMENT_STATE, fragmentSavedState)
        super.onSaveInstanceState(outState)
    }

    private fun selectTab(id: Int) {
        when (id) {
            0 -> {
                languageButton.visibility = View.VISIBLE
                NavigationHelper.getInstance().toHome(supportFragmentManager)
            }
            1 -> {
                languageButton.visibility = View.GONE
                NavigationHelper.getInstance().toSearch(supportFragmentManager)
            }
            2 -> {
                languageButton.visibility = View.VISIBLE
                NavigationHelper.getInstance().toLibrary(supportFragmentManager)
            }
        }
    }

    override fun onDestroy() {
        applicationContext.cacheDir.deleteRecursively()
        super.onDestroy()
    }


    open fun showLoadingDialog() {
        loadingDialog.show(supportFragmentManager, "LoadingDialog")
    }

    open fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    open fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
    }


}