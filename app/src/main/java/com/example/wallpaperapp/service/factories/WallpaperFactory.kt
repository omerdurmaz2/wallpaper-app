package com.example.wallpaperapp.service.factories

import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.WallPaperApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface WallpaperFactory {

    @GET("/api/")
    suspend fun getWallpapers(
        @Query("key") key: String? = WallPaperApi.apiKey,
        @Query("lang") lang: String? = Locale.getDefault().displayLanguage,
        @Query("q") search: String? = null,
        @Query("image_type") image_type: String? = "photo",
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("category") category: String? = null,
        @Query("order") order: String? = "popular"
    ): WallpaperResponse

}