package com.example.wallpaperapp.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class ScrollListener(private val linearLayoutManager: LinearLayoutManager, cPage: Int) :
    RecyclerView.OnScrollListener() {
    private var totalItemCount = 0
    var firstVisibleItem = 0
    var isLoading = false
    var currentPage = cPage


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {


        totalItemCount = linearLayoutManager.itemCount
        firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition() + 3


        if (firstVisibleItem == totalItemCount && !isLoading) {
            isLoading = true
            loadImages()
        }
        super.onScrolled(recyclerView, dx, dy)
    }

    abstract fun loadImages()

}