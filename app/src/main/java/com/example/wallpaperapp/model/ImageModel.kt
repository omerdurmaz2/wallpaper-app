package com.example.wallpaperapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class ImageModel : Serializable {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("pageURL")
    var pageURL: String? = null

    @SerializedName("largeImageURL")
    var largeImageURL: String? = null

    @SerializedName("previewURL")
    var previewURL: String? = null

    @SerializedName("webformatURL")
    var webformatURL: String? = null
}