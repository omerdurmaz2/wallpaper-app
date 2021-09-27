package com.example.wallpaperapp.view.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
) : ViewModel() {

    var categoryScrollPosition = 0
    val categories = listOf(
        "backgrounds",
        "fashion",
        "nature",
        "science",
        "education",
        "feelings",
        "health",
        "people",
        "religion",
        "places",
        "animals",
        "industry",
        "computer",
        "sports",
        "transportation",
        "travel",
        "buildings",
        "business",
        "music"
    )


}