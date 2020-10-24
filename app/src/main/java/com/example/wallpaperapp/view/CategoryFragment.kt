package com.example.wallpaperapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.example.wallpaperapp.model.WallpaperResponse
import com.example.wallpaperapp.service.NetworkCallback
import com.example.wallpaperapp.service.RestControllerFactory
import com.example.wallpaperapp.service.WallPaperApi
import com.example.wallpaperapp.util.NavigationHelper
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.ArrayList

class CategoryFragment : Fragment() {


    private var imageList: ArrayList<ImageModel> = ArrayList()
    private var imageListAdapter: ImageListAdapter? = null
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pullWallpapers(currentPage, null, MainActivity.selectedCategory.get())
    }


    private fun pullWallpapers(page: Int, query: String?, category: String?) {

        try {
            RestControllerFactory.instance.getWallpaperFactory()
                .getWallpapers(WallPaperApi.apiKey, query, "photo", page, 20, category)
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
        val gridLayoutManager = GridLayoutManager(activity?.applicationContext, 2)
        rvCategoryImages.layoutManager = gridLayoutManager
        imageListAdapter = ImageListAdapter(activity?.applicationContext, imageList) {
            openImage(it)
        }
        rvCategoryImages.adapter = imageListAdapter
    }

    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }

}