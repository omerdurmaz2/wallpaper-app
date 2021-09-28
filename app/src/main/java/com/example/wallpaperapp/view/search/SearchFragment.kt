package com.example.wallpaperapp.view.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaperapp.R
import com.example.wallpaperapp.base.BaseFragment
import com.example.wallpaperapp.databinding.FragmentSearchBinding
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.util.ext.gone
import com.example.wallpaperapp.util.ext.invisible
import com.example.wallpaperapp.util.ext.visible
import com.example.wallpaperapp.view.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate


    private val viewModel: SearchViewModel by viewModels()
    private var searchCategoryAdapter: SearchCategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            viewModel.categoryScrollPosition = savedInstanceState.getInt("categoryScrollPosition")
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        setUI()
        search()
        deleteSearchText()
        initRecyclerView()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("categoryScrollPosition", viewModel.categoryScrollPosition)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("CutPasteId")
    private fun setUI() {
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_search
       (activity as MainActivity).showBottomNavigation()
    }


    private fun actionListener(): TextView.OnEditorActionListener {
        return object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    binding.ivSearchButton.performClick()
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
                if (binding.etSearchImage.text.equals("") || binding.etSearchImage.text.isEmpty() || binding.etSearchImage.text == null) {
                    binding.ibDeleteText.invisible()
                } else {
                    binding.ibDeleteText.visible()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
    }

    private fun deleteSearchText() {
        binding.ibDeleteText.setOnClickListener {
            binding.ibDeleteText.invisible()
            binding.etSearchImage.text.clear()
            binding.etSearchImage.clearFocus()
        }
    }

    private fun initRecyclerView() {
        binding.rvCategories.visible()
        val gridLayoutManager = GridLayoutManager(activity?.applicationContext, 2)
        binding.rvCategories.layoutManager = gridLayoutManager
        searchCategoryAdapter = SearchCategoryAdapter(
            activity?.applicationContext,
            resources.getStringArray(R.array.categories),
            resources.getStringArray(R.array.category_colors)
        ) {


            MainActivity.category = viewModel.categories[it ?: 0]
            showLoadingIndicator()

            activity?.supportFragmentManager?.let { navigation ->
                NavigationHelper.getInstance().toSearchResults(navigation)
            }

        }
        binding.rvCategories.adapter = searchCategoryAdapter


        if (viewModel.categoryScrollPosition != 0)
            binding.rvCategories.scrollToPosition(viewModel.categoryScrollPosition)
        else
            viewModel.categoryScrollPosition = binding.rvCategories.verticalScrollbarPosition
    }

    private fun search() {

        binding.etSearchImage.addTextChangedListener(textWatcher())
        binding.etSearchImage.setOnEditorActionListener(actionListener())
        binding.ivSearchButton.setOnClickListener {
            if (binding.etSearchImage.text != null && binding.etSearchImage.text.trim() != "") {
                MainActivity.searchText = binding.etSearchImage.text.toString()
                binding.etSearchImage.text.forEach { _ ->
                    MainActivity.searchText.replace(".", "+")
                    MainActivity.searchText.replace(",", "+")
                    MainActivity.searchText.replace(" ", "+")
                    MainActivity.searchText.replace("-", "+")
                }
                binding.rvCategories.gone()
            }


            showLoadingIndicator()

            activity?.supportFragmentManager?.let { it1 ->
                NavigationHelper.getInstance().toSearchResults(
                    it1
                )
            }
        }

    }
}


