package com.example.wallpaperapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImageModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("pageURL")
    val pageURL: String?,
    @SerializedName("largeImageURL")
    val largeImageURL: String?,
    @SerializedName("previewURL")
    val previewURL: String?,
    @SerializedName("webformatURL")
    val webformatURL: String?
)