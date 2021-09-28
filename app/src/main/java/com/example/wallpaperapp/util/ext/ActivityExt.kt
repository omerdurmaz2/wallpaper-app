package com.example.wallpaperapp.util.ext

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT)
}
fun FragmentActivity.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG)
}