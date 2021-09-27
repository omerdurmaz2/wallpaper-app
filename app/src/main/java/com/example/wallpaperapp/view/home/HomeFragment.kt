package com.example.wallpaperapp.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
import com.example.wallpaperapp.service.WallPaperApi
import com.example.wallpaperapp.service.factories.WallpaperFactory
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.view.MainActivity
import com.example.wallpaperapp.util.ScrollListener
import com.example.wallpaperapp.util.ext.showToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
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
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null)
            viewModel.scrollPosition = savedInstanceState.getInt("scrollPosition")
        return super.onCreateView(inflater, container, savedInstanceState)

    }


    override fun init() {
        showLoadingIndicator()
        setUI()
        if (!viewModel.isloaded) {
            pullWallpapers(1)
            initRecyclerView()
        } else {
            initRecyclerView()
            Handler().postDelayed({
                (activity as MainActivity).hideLoadingDialog()
            }, 100)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("scrollPosition", viewModel.scrollPosition)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_home
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }


    private fun initRecyclerView() {
        binding.rvHome.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvHome.layoutManager = linearLayoutManager
        imageListAdapter = ImageListAdapter(activity?.applicationContext, viewModel.imageList) {
            openImage(it)
        }
        binding.rvHome.adapter = imageListAdapter

        if (viewModel.scrollPosition != 0)
            binding.rvHome.scrollToPosition(viewModel.scrollPosition)

        binding.rvHome.addOnScrollListener(object :
            ScrollListener(linearLayoutManager, viewModel.currentPage) {
            override fun loadImages() {
                currentPage++
                viewModel.currentPage = currentPage
                pullWallpapers(currentPage)
                Handler().postDelayed({
                    isLoading = false
                    viewModel.scrollPosition = firstVisibleItem - 3
                }, 500)
            }
        })

    }


    private fun addToImageList(images: ArrayList<ImageModel>) {
        val position = viewModel.imageList?.size
        viewModel.imageList?.addAll(images)
        position?.let { imageListAdapter?.notifyItemRangeInserted(it, position + 20) }
    }

    private fun pullWallpapers(page: Int) {
        showLoadingIndicator()
        viewModel.pullWallpapers(page) {
            when (it) {
                is DataState.Success<*> -> {
                    addToImageList(it as ArrayList<ImageModel>)
                    hideLoadingIndicator()
                }
                is DataState.Error -> {
                    hideLoadingIndicator()
                    showToast(getString(R.string.error_loading_images))
                }
            }
        }
    }


    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        (activity as MainActivity).showLoadingDialog()

        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }


}