package com.example.wallpaperapp.service

import android.content.Context
import android.net.ConnectivityManager
import com.example.wallpaperapp.service.factories.WallpaperFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestControllerFactory {

    private var wallpaperFactory: WallpaperFactory

    constructor() {
        val apiService: Retrofit = Retrofit.Builder().baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        wallpaperFactory = apiService.create(WallpaperFactory::class.java)
    }

    companion object {
        val instance = RestControllerFactory()
        const val timeoutInterval = 60L
        var client = OkHttpClient()

        fun hasInternetConnection(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }

        fun getWallpaperFactory(): WallpaperFactory {
        return wallpaperFactory
    }
}