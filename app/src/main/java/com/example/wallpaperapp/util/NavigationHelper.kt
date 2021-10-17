package com.example.wallpaperapp.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.wallpaperapp.R
import com.example.wallpaperapp.view.*
import com.example.wallpaperapp.view.home.HomeFragment
import com.example.wallpaperapp.view.library.LibraryFragment
import com.example.wallpaperapp.view.photo.PhotoFragment
import com.example.wallpaperapp.view.search.ResultFragment
import com.example.wallpaperapp.view.search.SearchFragment

class NavigationHelper {


    companion object {
        private val instance: NavigationHelper = NavigationHelper()
        fun getInstance(): NavigationHelper {
            return instance
        }
    }

    private constructor()


    fun toHome(
        fm: FragmentManager
    ) {
        val homeFragment = HomeFragment()
        replaceFragment(fm, R.id.flContent, homeFragment, null, "home", false)
    }

    fun toSearch(
        fm: FragmentManager
    ) {
        val searchFragment = SearchFragment()
        replaceFragment(fm, R.id.flContent, searchFragment, null, "search", false)
    }


    fun toLibrary(
        fm: FragmentManager
    ) {
        val profileFragment = LibraryFragment()
        replaceFragment(fm, R.id.flContent, profileFragment, null, "library", false)
    }

    fun toImageDetail(
        fm: FragmentManager
    ) {
        val photoFragment = PhotoFragment()
        replaceFragment(fm, R.id.flContent, photoFragment, null, "imageDetaile", true)
    }

    fun toSearchResults(
        fm: FragmentManager,
        args: Bundle? = null
    ) {
        val resultFragment = ResultFragment()
        resultFragment.arguments = args
        replaceFragment(fm, R.id.flContent, resultFragment, null, "searchResults", true)
    }

    private fun replaceFragment(
        fm: FragmentManager,
        id: Int,
        fragment: Fragment,
        stackText: String?,
        tag: String?,
        isAddToBackStack: Boolean
    ) {
        val currentFragment = fm.findFragmentById(R.id.flContent)
        if (!currentFragment?.tag.equals(tag)) {

            if (isAddToBackStack) {
                fragment.let {
                    fm.beginTransaction()
                        .setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                        .replace(id, it, tag).addToBackStack(stackText).commit()
                }
            } else {
                fragment.let {
                    fm.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(id, it, tag).commit()
                }
            }

        }


    }


}