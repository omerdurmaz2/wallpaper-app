package com.example.wallpaperapp.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.NetworkCallback
import com.example.wallpaperapp.service.RestControllerFactory
import com.example.wallpaperapp.service.WallPaperApi
import com.example.wallpaperapp.service.factories.WallpaperFactory
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.view.MainActivity
import com.example.wallpaperapp.util.ScrollListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.util.*


class HomeFragment : Fragment() {

    private var imageListAdapter: ImageListAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    companion object {
        var currentPage = 1
        var scrollPosition: Int = 0
        var imageList: ArrayList<ImageModel>? = ArrayList()
        var isloaded = false
        var totalImage = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null)
            scrollPosition = savedInstanceState.getInt("scrollPosition")

        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showLoadingDialog()

        setUI()
        initViews(view)
        if (!isloaded) {
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
        outState.putInt("scrollPosition", scrollPosition)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_home
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }


    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.rvHome)
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.layoutManager = linearLayoutManager
        imageListAdapter = ImageListAdapter(activity?.applicationContext, imageList) {
            openImage(it)
        }
        recyclerView.adapter = imageListAdapter

        if (scrollPosition != 0)
            recyclerView.scrollToPosition(scrollPosition)

        recyclerView.addOnScrollListener(object : ScrollListener(linearLayoutManager, currentPage) {
            override fun loadImages() {
                currentPage++
                Companion.currentPage = currentPage
                pullWallpapers(currentPage)
                Handler().postDelayed({
                    isLoading = false
                    scrollPosition = firstVisibleItem - 3
                }, 500)
            }
        })

    }


    private fun addToImageList(images: ArrayList<ImageModel>) {
        val position = imageList?.size
        imageList?.addAll(images)
        position?.let { imageListAdapter?.notifyItemRangeInserted(it, position + 20) }
    }

    private fun pullWallpapers(page: Int) {
        try {

            RestControllerFactory.instance.getWallpaperFactory()
                .getWallpapers(
                    WallPaperApi.apiKey, Locale.getDefault().displayLanguage,
                    null, "photo", page, 20, null
                )
                .enqueue(object : NetworkCallback<WallpaperResponse>() {
                    @SuppressLint("SetTextI18n", "ShowToast")
                    override fun onResponse(
                        call: Call<WallpaperResponse>,
                        response: Response<WallpaperResponse>
                    ) {

                        if (response.isSuccessful) {
                            totalImage += 20
                            if (response.body().hits?.size != 0) {
                                addToImageList(response.body().hits as ArrayList<ImageModel>)
                                isloaded = true
                                (activity as MainActivity).hideLoadingDialog()

                            }
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
                            "Fotoğraflar Yüklenirken Hata: " + t.localizedMessage + "\n" + t.message
                        )
                    }
                })
        } catch (e: Exception) {
        }
    }


    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        (activity as MainActivity).showLoadingDialog()

        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }


}