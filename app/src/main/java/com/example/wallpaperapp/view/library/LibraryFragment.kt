package com.example.wallpaperapp.view.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.base.BaseFragment
import com.example.wallpaperapp.databinding.FragmentLibraryBinding
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.util.ext.gone
import com.example.wallpaperapp.util.ext.showToast
import com.example.wallpaperapp.util.ext.visible
import com.example.wallpaperapp.view.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference


class LibraryFragment : BaseFragment<FragmentLibraryBinding>(R.layout.fragment_library) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLibraryBinding
        get() = FragmentLibraryBinding::inflate


    companion object {
        var fileList: ArrayList<File> = ArrayList()
    }


    override fun init() {
        setUI()
        showLoadingIndicator()
        viewLifecycleOwner.lifecycleScope.launch {
            setDirectory()
        }
    }


    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_library
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }

    private fun setDirectory() {

        val fullpath = File(
            Environment.getExternalStorageDirectory().absolutePath + "/${Environment.DIRECTORY_PICTURES}" + "/${
                getString(
                    R.string.app_name
                )
            }"
        )
        getFiles(fullpath)
    }

    private fun getFiles(root: File) {
        fileList.clear()
        val listAllFiles = root.listFiles()
        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".jpg")) {
                    fileList.add(currentFile.absoluteFile)
                }
            }
            bindImages(fileList)
        } else {
            binding.rvLibrary.gone()
            binding.llLibraryNoResult.visible()
        }

        hideLoadingIndicator()

    }

    private fun bindImages(fileList: ArrayList<File>) {
        binding.rvLibrary.visible()
        binding.llLibraryNoResult.gone()
        val libraryImageAdapter = LibraryImageAdapter(activity?.applicationContext, fileList, {
            openImage(it?.absolutePath.toString())
        }) {
            showToast("Uzun basıldı")
        }

        binding.rvLibrary.apply {
            layoutManager = GridLayoutManager(activity?.applicationContext, 2)
            adapter = libraryImageAdapter
        }
    }

    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(true)
        showLoadingIndicator()
        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }

}