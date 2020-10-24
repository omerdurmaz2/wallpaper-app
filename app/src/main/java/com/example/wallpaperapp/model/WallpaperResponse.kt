package com.example.wallpaperapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class WallpaperResponse : Serializable {
    @SerializedName("total")
    var total: Int? = null

    @SerializedName("totalHits")
    var totalHits: Int? = null

    @SerializedName("hits")
    var hits: List<ImageModel>? = null
}