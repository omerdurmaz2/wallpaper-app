package com.example.wallpaperapp.service

import android.content.Context
import android.net.ConnectivityManager
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.example.wallpaperapp.BuildConfig
import com.example.wallpaperapp.WallpaperApp
import com.example.wallpaperapp.service.factories.WallpaperFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class RestControllerFactory() {

    private var wallpaperFactory: WallpaperFactory

    init {
        val logging = HttpLoggingInterceptor()

        val chuckerCollector = ChuckerCollector(
            context = WallpaperApp.getApplicationContext(),
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        val chuckerInterceptor = ChuckerInterceptor.Builder(WallpaperApp.getApplicationContext())
            .collector(chuckerCollector)
            .alwaysReadResponseBody(true)
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging).addInterceptor(chuckerInterceptor)
            .connectTimeout(timeoutInterval, TimeUnit.SECONDS)
            .readTimeout(timeoutInterval, TimeUnit.SECONDS)

        client = okHttpClient.build()

        val apiService: Retrofit = Retrofit.Builder().baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
        wallpaperFactory = apiService.create(WallpaperFactory::class.java)
    }

    companion object {
        const val timeoutInterval = 300L
        var client = OkHttpClient()

    }

    fun getWallpaperFactory(): WallpaperFactory {
        return wallpaperFactory
    }
}