package com.example.wallpaperapp.view.home

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.base.BaseFragment
import com.example.wallpaperapp.base.DataState
import com.example.wallpaperapp.databinding.FragmentHomeBinding
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.NetworkCallback
import com.example.wallpaperapp.service.RestControllerFactory
import com.example.wallpaperapp.service.ResultWrapper
import com.example.wallpaperapp.service.WallPaperApi
import com.example.wallpaperapp.service.factories.WallpaperFactory
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.view.MainActivity
import com.example.wallpaperapp.util.ScrollListener
import com.example.wallpaperapp.util.ext.showToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.util.*


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate


    private var imageListAdapter: ImageListAdapter? = null
    private val viewModel: HomeViewModel by viewModels()


    override fun init() {
        setUI()
        initRecyclerView()
        pullWallpapers()
        viewModel.fetchResult.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Success<*> -> {
                    viewModel.isLoading = false
                    val list = (it.value as WallpaperResponse).hits
                    if (list?.isEmpty() == true) viewModel.shouldLoad = false
                    else {

                        if (viewModel.imageList.isNotEmpty()) viewModel.imageList.removeAt(viewModel.totalCount - 1)
                        list?.let { it1 -> viewModel.imageList.addAll(it1) }
                        viewModel.imageList.add(null)

                        list?.size?.let { it1 ->
                            imageListAdapter?.notifyItemRangeInserted(
                                viewModel.totalCount,
                                it1
                            )
                        }
                        viewModel.totalCount = viewModel.imageList.size
                    }
                }
                is ResultWrapper.GenericError -> {

                }
                is ResultWrapper.NetworkError -> {
                    showToast(getString(R.string.internet_connection_warning))
                    activity?.onBackPressed()
                }
                null -> {

                }
            }
        }
    }


    private fun setUI() {
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_home
        (activity as MainActivity).showBottomNavigation()
    }


    private fun initRecyclerView() {
        imageListAdapter = ImageListAdapter(activity?.applicationContext, viewModel.imageList) {
            openImage(it)
        }
        binding.rvHome.apply {
            adapter = imageListAdapter
            layoutManager = LinearLayoutManager(context)
        }


        binding.nsHome.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!viewModel.isLoading && viewModel.shouldLoad) {
                    pullWallpapers()
                }
            }
        })
    }

    private fun pullWallpapers() {
        viewModel.pullWallpapers()
    }


    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        showLoadingIndicator()
        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }


}