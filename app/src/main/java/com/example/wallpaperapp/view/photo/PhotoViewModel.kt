package com.example.wallpaperapp.view.photo

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.wallpaperapp.R
import com.example.wallpaperapp.base.DataState
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.NetworkCallback
import com.example.wallpaperapp.service.RestControllerFactory
import com.example.wallpaperapp.service.WallPaperApi
import com.example.wallpaperapp.util.ext.showToast
import com.example.wallpaperapp.view.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val restControllerFactory: RestControllerFactory
) : ViewModel() {


}