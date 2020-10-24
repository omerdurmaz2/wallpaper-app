package com.example.wallpaperapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.util.NavigationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_library.*
import java.io.File
import java.lang.ref.WeakReference


class LibraryFragment : Fragment() {


    private var fileList: ArrayList<File> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()

        recyclerView = view.findViewById(R.id.rvLibrary)
        setDirectory()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
        val listAllFiles = root.listFiles()
        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".jpg")) {
                    fileList.add(currentFile.absoluteFile)
                }
            }
            bindImages(fileList)
        } else {
            Log.i("sss", "Klasör Boş")
        }
    }

    private fun bindImages(fileList: ArrayList<File>) {
        val libraryImageAdapter = LibraryImageAdapter(activity?.applicationContext, fileList, {
            MainActivity.isLocal = WeakReference(true)
            MainActivity.selectedImage = WeakReference(it?.absolutePath.toString())
            activity?.supportFragmentManager?.let {
                NavigationHelper.getInstance().toImageDetail(it)
            }
        }) {
            Toast.makeText(context, "Uzun basıldı", Toast.LENGTH_SHORT)
        }
        val gridLayoutManager = GridLayoutManager(activity?.applicationContext, 2)

        recyclerView.layoutManager = gridLayoutManager

        recyclerView.adapter = libraryImageAdapter
    }


    @SuppressLint("CutPasteId")
    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId =
            R.id.bottom_navigation_library
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }
}