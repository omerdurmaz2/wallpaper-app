package com.example.wallpaperapp.service.factories

import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.WallPaperApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WallpaperFactory {

    @GET("/api/")
    fun getWallpapers(
        @Query("key") key: String? = WallPaperApi.apiKey,
        @Query("q") search: String?,
        @Query("image_type") image_type: String? = "photo",
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("category") category: String?
    ): Call<WallpaperResponse>

}