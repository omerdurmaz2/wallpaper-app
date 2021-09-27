package com.example.wallpaperapp.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wallpaperapp.R
import com.example.wallpaperapp.base.BaseFragment
import com.example.wallpaperapp.base.DataState
import com.example.wallpaperapp.databinding.FragmentResultBinding
import com.example.wallpaperapp.model.ImageModel
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
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var imageListAdapter: ImageListAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            viewModel.imageScrollPosition = savedInstanceState.getInt("imageScrollPosition")
            viewModel.currentPage = savedInstanceState.getInt("currentPage")
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        setUI()
        if (!viewModel.isloaded)
            pullWallpapers(viewModel.currentPage, MainActivity.searchText, null)
        initRecyclerView()
    }


    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_search
        (activity as MainActivity).showBottomNavigation()
    }


    override fun initClickListeners() {
        super.initClickListeners()
        binding.btnResultBack.setOnClickListener { activity?.onBackPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("imageScrollPosition", viewModel.imageScrollPosition)
        outState.putInt("currentPage", viewModel.currentPage)
        super.onSaveInstanceState(outState)
    }


    private fun initRecyclerView() {

        binding.llSearchNoResult.invisible()
        binding.rvSearch.visible()
        binding.rvSearch.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvSearch.layoutManager = linearLayoutManager
        imageListAdapter = ImageListAdapter(activity?.applicationContext, viewModel.imageList) {
            openImage(it)
        }
        binding.rvSearch.adapter = imageListAdapter

        if (viewModel.imageScrollPosition != 0)
            binding.rvSearch.scrollToPosition(viewModel.imageScrollPosition)
        else
            viewModel.imageScrollPosition = binding.rvSearch.verticalScrollbarPosition

        binding.rvSearch.addOnScrollListener(object :
            ScrollListener(linearLayoutManager, viewModel.currentPage) {
            override fun loadImages() {
                currentPage++
                viewModel.currentPage = currentPage
                pullWallpapers(currentPage, MainActivity.searchText, null)

            }
        })


    }


    private fun addToImageList(images: ArrayList<ImageModel>) {
        val position = viewModel.imageList.size
        viewModel.imageList.addAll(images)
        position.let { imageListAdapter?.notifyItemRangeInserted(it, position + 20) }
    }


    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        (activity as MainActivity).showLoadingDialog()

        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }

    private fun pullWallpapers(page: Int, query: String? = "flower", category: String?) {
        showLoadingIndicator()
        viewModel.pullWallpapers(page, query, category) {
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
}