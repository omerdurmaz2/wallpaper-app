package com.example.wallpaperapp.util.ext

import androidx.fragment.app.Fragment
import com.example.wallpaperapp.view.MainActivity

fun Fragment.showToast(message: String) = (activity as MainActivity).showToast(message)

fun Fragment.showLongToast(message: String) = (activity as MainActivity).showLongToast(message)