package com.example.wallpaperapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
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
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_search_category.*
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.ArrayList


class SearchFragment : Fragment() {

    private var imageList: ArrayList<ImageModel> = ArrayList()
    private var imageListAdapter: ImageListAdapter? = null
    private var searchCategoryAdapter: SearchCategoryAdapter? = null
    private var currentPage = 1
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var recyclerViewSearchResult: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewCategory = view.findViewById(R.id.rvCategories)
        recyclerViewSearchResult = view.findViewById(R.id.rvSearch)

        setUI()

        ivSearchButton.setOnClickListener {
            if (etSearchImage.text != null && etSearchImage.text.trim() != "") {
                val query = etSearchImage.text.toString()
                etSearchImage.text.forEach { _ ->
                    query.replace(".", "+")
                    query.replace(",", "+")
                    query.replace(" ", "+")
                    query.replace("-", "+")
                }
                recyclerViewCategory.visibility = View.GONE
                recyclerViewSearchResult.visibility = View.VISIBLE
                pullWallpapers(currentPage, query, null)
            } else pullWallpapers(currentPage, null, null)
        }

        etSearchImage.addTextChangedListener(textWatcher())
        etSearchImage.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if ((p2 != null && p2.keyCode == KeyEvent.KEYCODE_ENTER) || p1 == EditorInfo.IME_ACTION_SEARCH) {
                    etSearchImage.performClick()
                }
                return false
            }

        })
    }

    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_search
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE

        loadCategories()
    }

    private fun textWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (etSearchImage.text.equals("") && etSearchImage.text.isEmpty()) {
                    recyclerViewCategory.visibility = View.VISIBLE
                    recyclerViewSearchResult.visibility = View.GONE
                }
            }
        }
    }

    private fun loadCategories() {
        val gridLayoutManager = GridLayoutManager(activity?.applicationContext, 2)
        recyclerViewCategory.layoutManager = gridLayoutManager
        searchCategoryAdapter = SearchCategoryAdapter(
            activity?.applicationContext,
            resources.getStringArray(R.array.categories),
            resources.getStringArray(R.array.category_images)
        ) { it ->
            MainActivity.selectedCategory = WeakReference(it)
            activity?.supportFragmentManager?.let { navigation ->
                NavigationHelper.getInstance().toCategoryImages(navigation)
            }

        }
        recyclerViewCategory.adapter = searchCategoryAdapter
    }

    private fun pullWallpapers(page: Int, query: String? = "flower", category: String?) {
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
        recyclerViewSearchResult.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(activity?.applicationContext, 2)
        recyclerViewSearchResult.layoutManager = gridLayoutManager
        imageListAdapter = ImageListAdapter(activity?.applicationContext, imageList) {
            openImage(it)
        }
        recyclerViewSearchResult.adapter = imageListAdapter
        //initRecyclerViewScrollManager()
    }

    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(false)
        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }


}


