package com.example.wallpaperapp.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.NetworkCallback
import com.example.wallpaperapp.service.RestControllerFactory
import com.example.wallpaperapp.service.WallPaperApi
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.util.ScrollListener
import com.example.wallpaperapp.view.MainActivity
import com.example.wallpaperapp.view.home.ImageListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*


class ResultFragment : Fragment() {

    private lateinit var recyclerViewSearchResult: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var llNoResult: LinearLayout
    lateinit var backButton: ImageButton
    private var imageListAdapter: ImageListAdapter? = null
    private var imageScrollPosition = 0
    private var currentPage = 1
    private var imageList: ArrayList<ImageModel> = ArrayList()

    companion object {
        var isLoaded = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            imageScrollPosition = savedInstanceState.getInt("imageScrollPosition")
            currentPage = savedInstanceState.getInt("currentPage")
        }
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        initViews(view)
        onBackPressed()
        if (!isLoaded)
            pullWallpapers(currentPage, MainActivity.searchText, null)
        initRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_search
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }

    private fun initViews(view: View) {
        recyclerViewSearchResult = view.findViewById(R.id.rvSearch)
        llNoResult = view.findViewById(R.id.llSearchNoResult)
        backButton = view.findViewById(R.id.fragmentResultBackButton)
    }

    private fun onBackPressed() {
        backButton.setOnClickListener { activity?.onBackPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("imageScrollPosition", imageScrollPosition)
        outState.putInt("currentPage", currentPage)
        super.onSaveInstanceState(outState)
    }


    private fun pullWallpapers(page: Int, query: String? = "flower", category: String?) {

        try {
            RestControllerFactory.instance.getWallpaperFactory()
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
                            if (response.body().hits?.size != 0) {
                                addToImageList(response.body().hits as ArrayList<ImageModel>)
                                isLoaded = true


                            } else {
                                llNoResult.visibility = View.VISIBLE
                                recyclerViewSearchResult.visibility = View.INVISIBLE
                            }

                            (activity as MainActivity).hideLoadingDialog()

                        }

                    }

                    override fun onFailure(
                        call: Call<WallpaperResponse>,
                        t: Throwable
                    ) {
                        (activity as MainActivity).hideLoadingDialog()
                        (activity as MainActivity).showToast(getString(R.string.error_loading_images))
                        Log.e(
                            "sss",
                            "Resim yüklenirken hata: " + t.localizedMessage + "\n" + t.message
                        )
                    }
                })

        } catch (e: Exception) {
        }
    }

    private fun initRecyclerView() {

        llNoResult.visibility = View.INVISIBLE
        recyclerViewSearchResult.visibility = View.VISIBLE

        recyclerViewSearchResult.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerViewSearchResult.layoutManager = linearLayoutManager
        imageListAdapter = ImageListAdapter(activity?.applicationContext, imageList) {
            openImage(it)
        }
        recyclerViewSearchResult.adapter = imageListAdapter

        if (imageScrollPosition != 0)
            recyclerViewSearchResult.scrollToPosition(imageScrollPosition)
        else
            imageScrollPosition = recyclerViewSearchResult.verticalScrollbarPosition

        recyclerViewSearchResult.addOnScrollListener(object :
            ScrollListener(linearLayoutManager, currentPage) {
            override fun loadImages() {
                currentPage++
                this@ResultFragment.currentPage = currentPage
                pullWallpapers(currentPage, MainActivity.searchText, null)

            }
        })


    }

    private fun addToImageList(images: ArrayList<ImageModel>) {
        val position = imageList.size
        imageList.addAll(images)
        position.let { imageListAdapter?.notifyItemRangeInserted(it, position + 20) }
    }


    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        (activity as MainActivity).showLoadingDialog()

        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }
}