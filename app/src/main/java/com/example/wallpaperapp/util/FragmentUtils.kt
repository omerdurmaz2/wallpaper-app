package com.example.wallpaperapp.util

import android.content.Context
import android.util.Log
import com.example.wallpaperapp.view.MainActivity
import com.example.wallpaperapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentUtils {

    companion object {
        fun changeNavigationSelection(context: Context, itemId: Int) {
            try {
                Log.i("sss", "selected tab:$itemId")
                Log.i("sss", "Hello world")
                val bottomNavigationView =
                    (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)

                Log.i("sss", "selecteItemId:" + bottomNavigationView.selectedItemId)


                bottomNavigationView.selectedItemId = itemId


            } catch (e: ClassCastException) {

            }
        }


    }
}