package com.example.wallpaperapp.util.ext

import android.view.View
import androidx.fragment.app.Fragment
import com.example.wallpaperapp.view.MainActivity

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}