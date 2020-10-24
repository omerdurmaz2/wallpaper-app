package com.example.wallpaperapp.view

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.NetworkCallback
import com.example.wallpaperapp.service.RestControllerFactory
import com.example.wallpaperapp.service.WallPaperApi
import com.example.wallpaperapp.util.NavigationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.ArrayList


class HomeFragment : Fragment() {

    private var imageList: ArrayList<ImageModel> = ArrayList()
    private var imageListAdapter: ImageListAdapter? = null
    private var isLoading = false
    private var currentPage = 1
    private var previousTotal = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()

        recyclerView = view.findViewById(R.id.rvHome)
        setRecyclerViewLayout()
        pullWallpapers(currentPage)
    }

    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_home
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }

    private fun pullWallpapers(page: Int) {
        try {
            RestControllerFactory.instance.getWallpaperFactory()
                .getWallpapers(WallPaperApi.apiKey, "car", "photo", 1, 20, null)
                .enqueue(object : NetworkCallback<WallpaperResponse>() {
                    @SuppressLint("SetTextI18n", "ShowToast")
                    override fun onResponse(
                        call: Call<WallpaperResponse>,
                        response: Response<WallpaperResponse>
                    ) {

                        if (response.isSuccessful) {
                            if (response.body().hits?.size != 0) {
                                imageList = response.body().hits as ArrayList<ImageModel>
                                setRecyclerViewLayout()
                            }
                        }

                    }

                    override fun onFailure(
                        call: Call<WallpaperResponse>,
                        t: Throwable
                    ) {
                        Log.i("sss", "Hata: " + t.localizedMessage + "\n" + t.message)
                    }
                })

        } catch (e: Exception) {
        }
    }

    private fun setRecyclerViewLayout() {
        //        rvHome.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.layoutManager = linearLayoutManager
        imageListAdapter = ImageListAdapter(activity?.applicationContext, imageList) {
            openImage(it)
        }
        recyclerView.adapter = imageListAdapter
        //initRecyclerViewScrollManager()
    }

    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }

    private fun addToImageList(images: ArrayList<ImageModel>) {
        val position = imageList?.size
        imageList.addAll(images)
        imageListAdapter?.notifyItemRangeInserted(position, position + 20)
    }

    private fun initRecyclerViewScrollManager() {
        rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var visibleItemCount = linearLayoutManager.childCount
                var totalItemCount = linearLayoutManager.itemCount
                var firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                Log.i(
                    "sss",
                    "isLoading: $isLoading, totalItemCount: $totalItemCount,visibleItemCount: $visibleItemCount, currentPage: $currentPage, previousTotal: $previousTotal, firstVisibleItem: $firstVisibleItem"
                )


/*                if (isLoading && totalItemCount > previousTotal) {
                    isLoading = false
                    previousTotal = totalItemCount
                }
                val visibleTreshold = 3 * currentPage
                if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleTreshold)) {
                    currentPage++
                    pullWallpapers(currentPage)
                    isLoading = true
                }*/
            }
        })
    }


}