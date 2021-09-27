package com.example.wallpaperapp.view.search

import androidx.lifecycle.ViewModel
import com.example.wallpaperapp.base.DataState
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.NetworkCallback
import com.example.wallpaperapp.service.RestControllerFactory
import com.example.wallpaperapp.service.WallPaperApi
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val restControllerFactory: RestControllerFactory
) : ViewModel() {

    var scrollPosition: Int = 0
    var isloaded = false
    var totalImage = 0
    var imageScrollPosition = 0
    var currentPage = 1
    var imageList: ArrayList<ImageModel> = ArrayList()


     fun pullWallpapers(
        page: Int,
        query: String? = "flower",
        category: String?,
        callback: (DataState) -> Unit
    ) {
        restControllerFactory.getWallpaperFactory()
            .getWallpapers(
                WallPaperApi.apiKey,
                Locale.getDefault().displayLanguage,
                query,
                "photo",
                page,
                20,
                category
            )
            .enqueue(object : NetworkCallback<WallpaperResponse>() {
                override fun onResponse(
                    call: Call<WallpaperResponse>,
                    response: Response<WallpaperResponse>
                ) {

                    if (response.isSuccessful) {
                        if (response.body()?.hits?.size != 0) {
                            isloaded = true
                            callback(DataState.Success(response.body()?.hits as ArrayList<ImageModel>))
                        } else {
                            callback(DataState.Error(""))
                        }
                    } else callback(DataState.Error(""))
                }

                override fun onFailure(
                    call: Call<WallpaperResponse>,
                    t: Throwable
                ) {
                    callback(DataState.Error(""))
                }
            })


    }


}