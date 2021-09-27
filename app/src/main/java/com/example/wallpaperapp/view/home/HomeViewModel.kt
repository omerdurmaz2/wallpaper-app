package com.example.wallpaperapp.view.home

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
class HomeViewModel @Inject constructor(
    private val restControllerFactory: RestControllerFactory
) : ViewModel() {

    var currentPage = 1
    var scrollPosition: Int = 0
    var imageList: ArrayList<ImageModel>? = ArrayList()
    var isloaded = false
    var totalImage = 0

    fun pullWallpapers(page: Int, callback: (DataState) -> Unit) {
        try {

            restControllerFactory.getWallpaperFactory()
                .getWallpapers(
                    WallPaperApi.apiKey, Locale.getDefault().displayLanguage,
                    null, "photo", page, 20, null
                )
                .enqueue(object : NetworkCallback<WallpaperResponse>() {
                    @SuppressLint("SetTextI18n", "ShowToast")
                    override fun onResponse(
                        call: Call<WallpaperResponse>,
                        response: Response<WallpaperResponse>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body()?.hits?.size != 0) {
                                totalImage += 20
                                callback(DataState.Success(response.body()?.hits))
                                isloaded = true
                            }else {
                                callback(DataState.Success(arrayListOf<ImageModel>()))
                            }
                        }else{
                            callback(DataState.Error(""))
                        }
                    }

                    override fun onFailure(
                        call: Call<WallpaperResponse>,
                        t: Throwable
                    ) {
                        callback(DataState.Error(""))
                    }
                })
        } catch (e: Exception) {
        }
    }


}