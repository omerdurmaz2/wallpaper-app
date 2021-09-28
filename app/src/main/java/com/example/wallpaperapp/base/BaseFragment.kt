package com.example.wallpaperapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.wallpaperapp.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseFragment<VB : ViewBinding>(
    private val layout: Int,
) : Fragment() {

    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(layoutInflater, container, false)
        return _binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initClickListeners()
        initFocusListeners()
    }

    /**
     * initialize the fragment
     */
    abstract fun init()
    open fun initClickListeners() {}
    open fun initFocusListeners() {}


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    open fun showLoadingIndicator() {
        (activity as MainActivity).showLoadingDialog()
    }

    open fun hideLoadingIndicator() {
        (activity as MainActivity).hideLoadingDialog()
    }


}