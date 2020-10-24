package com.example.wallpaperapp.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.wallpaperapp.R
import com.example.wallpaperapp.view.*

class NavigationHelper {

    companion object {

        private val instance: NavigationHelper = NavigationHelper()
        var selectedCategory: String = ""
        fun getInstance(): NavigationHelper {
            return instance
        }

    }

    private constructor()


    fun toHome(
        fm: FragmentManager
    ) {
        val homeFragment = HomeFragment()
        replaceFragment(fm, R.id.flContent, homeFragment, null, false)
    }

    fun toSearch(
        fm: FragmentManager
    ) {
        val searchFragment = SearchFragment()
        replaceFragment(fm, R.id.flContent, searchFragment, null, false)
    }


    fun toProfile(
        fm: FragmentManager
    ) {
        val profileFragment = LibraryFragment()
        replaceFragment(fm, R.id.flContent, profileFragment, null, false)
    }

    fun toImageDetail(
        fm: FragmentManager
    ) {
        val photoFragment = PhotoFragment()
        replaceFragment(fm, R.id.flContent, photoFragment, null, true)
    }

    fun toCategoryImages(
        fm: FragmentManager
    ) {
        val categoryFragment = CategoryFragment()
        replaceFragment(fm, R.id.flContent, categoryFragment, null, true)
    }

    private fun replaceFragment(
        fm: FragmentManager,
        id: Int,
        fragment: Fragment,
        stackText: String?,
        isAddToBackStack: Boolean
    ) {
        if (isAddToBackStack) {
            fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(id, fragment).addToBackStack(stackText)
                .commit()

        } else {
            fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(id, fragment).commitAllowingStateLoss()
        }
    }
}