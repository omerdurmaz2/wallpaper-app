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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.util.NavigationHelper
import com.example.wallpaperapp.view.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.lang.ref.WeakReference


class LibraryFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var llNoResult: LinearLayout
    var scrollPosition = 0

    companion object {
        var fileList: ArrayList<File> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null)
            scrollPosition = savedInstanceState.getInt("scrollPosition")
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        initViews(view)
        (activity as MainActivity).showLoadingDialog()
        setDirectory()
        (activity as MainActivity).hideLoadingDialog()

    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.rvLibrary)
        llNoResult = view.findViewById(R.id.llLibraryNoResult)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("scrollPosition", scrollPosition)
        super.onSaveInstanceState(outState)
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
            recyclerView.visibility = View.GONE
            llNoResult.visibility = View.VISIBLE
        }

    }

    private fun bindImages(fileList: ArrayList<File>) {
        recyclerView.visibility = View.VISIBLE
        llNoResult.visibility = View.GONE
        val libraryImageAdapter = LibraryImageAdapter(activity?.applicationContext, fileList, {
            openImage(it?.absolutePath.toString())
        }) {

            Toast.makeText(context, "Uzun basıldı", Toast.LENGTH_SHORT)
        }
        val gridLayoutManager = GridLayoutManager(activity?.applicationContext, 2)

        recyclerView.layoutManager = gridLayoutManager

        recyclerView.adapter = libraryImageAdapter
        if (scrollPosition != 0)
            recyclerView.scrollToPosition(scrollPosition)
        else
            scrollPosition = recyclerView.verticalScrollbarPosition


    }

    private fun openImage(image: String?) {
        MainActivity.selectedImage = WeakReference(image ?: "")
        MainActivity.isLocal = WeakReference(true)
        (activity as MainActivity).showLoadingDialog()

        activity?.supportFragmentManager?.let { NavigationHelper.getInstance().toImageDetail(it) }
    }

}