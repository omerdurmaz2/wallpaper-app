package com.example.wallpaperapp.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpaperapp.base.DataState
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val restControllerFactory: RestControllerFactory
) : ViewModel() {

    var totalCount = 0
    var currentPage = 0
    var shouldLoad = true
    var isLoading = false
    var searchText: String? = null
    var category: String? = null
    var imageList: ArrayList<ImageModel?> = ArrayList()
    private val _fetchResult = MutableLiveData<ResultWrapper<WallpaperResponse>>()
    val fetchResult: LiveData<ResultWrapper<WallpaperResponse>> get() = _fetchResult

    fun pullWallpapers() {
        currentPage++
        isLoading = true
        viewModelScope.launch {
            _fetchResult.postValue(NetworkHelper.safeApiCall(Dispatchers.IO) {
                restControllerFactory.getWallpaperFactory().getWallpapers(
                    page = currentPage,
                    search = searchText,
                    category = category
                )
            })
        }
    }


}