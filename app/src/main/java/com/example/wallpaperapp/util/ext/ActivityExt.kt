package com.example.wallpaperapp.util.ext

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT)
}
fun AppCompatActivity.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG)
}