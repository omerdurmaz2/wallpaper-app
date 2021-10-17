package com.example.wallpaperapp.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wallpaperapp.R
import com.example.wallpaperapp.base.BaseFragment
import com.example.wallpaperapp.base.DataState
import com.example.wallpaperapp.databinding.FragmentResultBinding
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.ResultWrapper
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.util.ScrollListener
import com.example.wallpaperapp.util.ext.invisible
import com.example.wallpaperapp.util.ext.showToast
import com.example.wallpaperapp.util.ext.visible
import com.example.wallpaperapp.view.MainActivity
import com.example.wallpaperapp.view.home.ImageListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.*

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>(R.layout.fragment_result) {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResultBinding
        get() = FragmentResultBinding::inflate


    private val viewModel: ResultViewModel by viewModels()
    private var imageListAdapter: ImageListAdapter? = null


    override fun init() {
        arguments?.let {
            viewModel.searchText = it.getString("searchText")
            viewModel.category = it.getString("category")
        }

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
            R.id.bottom_navigation_search
        (activity as MainActivity).showBottomNavigation()
    }


    override fun initClickListeners() {
        super.initClickListeners()
        binding.btnResultBack.setOnClickListener { activity?.onBackPressed() }
    }

    private fun initRecyclerView() {

        binding.llSearchNoResult.invisible()
        binding.rvSearch.visible()

        imageListAdapter = ImageListAdapter(activity?.applicationContext, viewModel.imageList) {
            openImage(it)
        }
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = imageListAdapter
        }
        binding.nsResult.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!viewModel.isLoading && viewModel.shouldLoad) {
                    pullWallpapers()
                }
            }
        })

    }

    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        showLoadingIndicator()

        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }

    private fun pullWallpapers() {
        viewModel.pullWallpapers()
    }
}