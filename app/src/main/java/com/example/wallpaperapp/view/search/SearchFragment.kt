package com.example.wallpaperapp.view.search

import android.annotation.SuppressLint

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.view.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var etSearchText: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var btnDeleteText: ImageButton
    private var searchCategoryAdapter: SearchCategoryAdapter? = null
    private var categoryScrollPosition = 0
    private val CATEGORIES = listOf(
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            categoryScrollPosition = savedInstanceState.getInt("categoryScrollPosition")
        }

        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        initViews(view)
        search()
        deleteSearchText()
        initRecyclerView()
    }

    private fun initViews(view: View) {
        recyclerViewCategory = view.findViewById(R.id.rvCategories)
        etSearchText = view.findViewById(R.id.etSearchImage)
        btnSearch = view.findViewById(R.id.ivSearchButton)
        btnDeleteText = view.findViewById(R.id.ibDeleteText)

    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("categoryScrollPosition", categoryScrollPosition)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_search
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE

    }


    private fun search() {
        btnSearch.setOnClickListener {
            if (etSearchText.text != null && etSearchImage.text.trim() != "") {
                MainActivity.searchText = etSearchImage.text.toString()
                etSearchText.text.forEach { _ ->
                    MainActivity.searchText.replace(".", "+")
                    MainActivity.searchText.replace(",", "+")
                    MainActivity.searchText.replace(" ", "+")
                    MainActivity.searchText.replace("-", "+")
                }
                recyclerViewCategory.visibility = View.GONE
            }

            ResultFragment.isLoaded = false

            (activity as MainActivity).showLoadingDialog()

            activity?.supportFragmentManager?.let { it1 ->
                NavigationHelper.getInstance().toSearchResults(
                    it1
                )
            }
        }
        etSearchText.addTextChangedListener(textWatcher())
        etSearchText.setOnEditorActionListener(actionListener())
    }


    private fun actionListener(): TextView.OnEditorActionListener {
        return object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    btnSearch.performClick()
                    return true
                }
                return false
            }

        }
    }

    private fun textWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (etSearchText.text.equals("") || etSearchText.text.isEmpty() || etSearchText.text == null) {
                    btnDeleteText.visibility = View.INVISIBLE
                } else {
                    btnDeleteText.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
    }

    private fun deleteSearchText() {
        btnDeleteText.setOnClickListener {
            btnDeleteText.visibility = View.INVISIBLE
            etSearchText.text = null
            etSearchText.clearFocus()
        }
    }

    private fun initRecyclerView() {
        recyclerViewCategory.visibility = View.VISIBLE
        val gridLayoutManager = GridLayoutManager(activity?.applicationContext, 2)
        recyclerViewCategory.layoutManager = gridLayoutManager
        searchCategoryAdapter = SearchCategoryAdapter(
            activity?.applicationContext,
            resources.getStringArray(R.array.categories),
            resources.getStringArray(R.array.category_colors)
        ) {


            ResultFragment.isLoaded = false
            MainActivity.searchText = CATEGORIES[it!!]
            (activity as MainActivity).showLoadingDialog()

            activity?.supportFragmentManager?.let { navigation ->
                NavigationHelper.getInstance().toSearchResults(navigation)
            }

        }
        recyclerViewCategory.adapter = searchCategoryAdapter


        if (categoryScrollPosition != 0)
            recyclerViewCategory.scrollToPosition(categoryScrollPosition)
        else
            categoryScrollPosition = recyclerViewCategory.verticalScrollbarPosition
    }


}


