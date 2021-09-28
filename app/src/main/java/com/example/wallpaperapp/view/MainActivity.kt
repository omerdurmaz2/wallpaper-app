package com.example.wallpaperapp.view

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wallpaperapp.R
import com.example.wallpaperapp.databinding.ActivityMainBinding
import com.example.wallpaperapp.dialog.LanguageDialog
import com.example.wallpaperapp.dialog.LoadingDialog
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.util.PermissionUtil
import com.example.wallpaperapp.util.ext.gone
import com.example.wallpaperapp.util.ext.visible
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding? = null
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var languageDialog: LanguageDialog
    private lateinit var languageButton: ImageButton
    private val permissionUtil = PermissionUtil(this, this)
    private val fragmentState = "fragmentState"

    companion object {
        lateinit var selectedImage: WeakReference<String>
        lateinit var isLocal: WeakReference<Boolean>
        lateinit var fragmentSavedState: HashMap<String, Fragment.SavedState?>
        var searchText = ""
        var category: String? = null
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Picasso.setSingletonInstance(
            Picasso.Builder(this) // additional settings
                .build()
        )


        languageDialog()

        if (savedInstanceState == null) {
            fragmentSavedState = HashMap()
            selectTab(0)
            fixAllIcons()
        } else {
            fragmentSavedState =
                savedInstanceState.getSerializable(fragmentState) as HashMap<String, Fragment.SavedState?>
        }
        loadingDialog = LoadingDialog(this)
        loadingDialog.isCancelable = false
        loadLanguage()
        permissionUtil.checkPermissions {}

    }

    fun checkPermissions(callback: (Boolean) -> Unit) =
        permissionUtil.checkPermissions { callback(it) }

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
        outState.putSerializable(fragmentState, fragmentSavedState)
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
        binding = null
        super.onDestroy()
    }


    fun showLoadingDialog() {
        loadingDialog.show(supportFragmentManager, LoadingDialog::class.simpleName)
    }

    fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }


    fun showBottomNavigation() {
        binding?.bottomNavigationView?.visible()
    }

    fun hideBottomNavigation() {
        binding?.bottomNavigationView?.gone()
    }


}