package com.example.wallpaperapp.view.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.wallpaperapp.R
import com.example.wallpaperapp.base.BaseFragment
import com.example.wallpaperapp.databinding.FragmentPhotoBinding
import com.example.wallpaperapp.util.ext.gone
import com.example.wallpaperapp.view.MainActivity
import com.example.wallpaperapp.view.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PhotoFragment : BaseFragment<FragmentPhotoBinding>(R.layout.fragment_photo) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhotoBinding
        get() = FragmentPhotoBinding::inflate

    private val viewModel: PhotoViewModel by viewModels()

    private lateinit var savedImageDirectory: File
    private val setWallpaper = MutableLiveData<Boolean>()
    private var fileName = ""
    val REQUEST_CODE = 2000

    override fun init() {

        (activity as MainActivity).hideBottomNavigation()
        bindImage()
        if (MainActivity.isLocal.get() == true) binding.btnDownlaod.gone()

        Handler(Looper.getMainLooper()).postDelayed({
            hideLoadingIndicator()
        }, 500)

        setWallpaper.observe(viewLifecycleOwner, Observer {
            Log.e("sss", "observed")
            if (it)
                setWallPaper()
        })
    }


    private fun bindImage() {
        val circularProgress = context?.let { CircularProgressDrawable(it) }
        circularProgress?.strokeWidth = 5f
        circularProgress?.centerRadius = 30f
        circularProgress?.start()

        Picasso.get().apply {
            circularProgress?.let {
                if (MainActivity.isLocal.get() == true)
                    load(
                        File(
                            MainActivity.selectedImage.get() ?: ""
                        )
                    )
                else load(MainActivity.selectedImage.get() ?: "")
                .placeholder(it)
                .into(binding.ivSelectedImage)
            }
        }

    }


    override fun initClickListeners() {
        super.initClickListeners()
        binding.btnSetWallpaper.setOnClickListener {

            (activity as MainActivity).checkPermissions {
                if (it) {
                    MainActivity.selectedImage.get()?.let { it1 -> downloadImage(it1, true) }
                    showSnackBar(binding.root, getString(R.string.photo_wallpaper_set))
                }
            }

        }
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnDownlaod.setOnClickListener {
            (activity as MainActivity).checkPermissions {
                if (it) {
                    MainActivity.selectedImage.get()?.let { it1 -> downloadImage(it1) }
                }
            }
        }
    }


    private fun setWallPaper() {
        try {

            val storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getString(R.string.app_name)
            )

            val image = File(storageDir, fileName)
            Log.i("sss", "path: $image")


            val uriFile: Uri? =
                activity?.let {
                    getUriForFile(
                        it,
                        "com.example.wallpaperapp",
                        image
                    )
                }

            Log.i("sss", "path: $uriFile")


            val intent = Intent(Intent.ACTION_ATTACH_DATA)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setDataAndType(uriFile, "image/*")
            intent.putExtra(".jpg", "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "set wallpaper"))


        } catch (e: Exception) {
            Log.e("sss", "error: ${e.localizedMessage}")
        }
    }


    @SuppressLint("ResourceType")
    private fun showSnackBar(view: View, msg: String) {
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(resources.getInteger(R.color.colorPrimary))
        snackbar.show()
    }


    var msg: String? = ""
    var lastMsg = ""
    private fun downloadImage(imageURL: String, sWP: Boolean? = false) {

        Log.e("sss", "download image")
        try {
            var directory = File(Environment.DIRECTORY_PICTURES)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val downloadManager =
                activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val downloadUri = Uri.parse(imageURL)

            val request = DownloadManager.Request(downloadUri).apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE).setAllowedOverRoaming(
                    false
                ).setTitle(imageURL.substring(imageURL.lastIndexOf("/") + 1)).setDescription("")
                    .setDestinationInExternalPublicDir(
                        directory.toString(),
                        (getString(R.string.app_name) + "/" + imageURL.substring(
                            imageURL.lastIndexOf(
                                "/"
                            ) + 1
                        ))
                    )
            }

            val downloadId = downloadManager.enqueue(request)
            val query = DownloadManager.Query().setFilterById(downloadId)
            Thread(Runnable {
                var downloading = true
                while (downloading) {
                    val cursor: Cursor = downloadManager.query(query)
                    cursor.moveToFirst()
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                        if (sWP == true) {
                            setWallpaper.postValue(true)
                        }
                    }
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    msg = statusMessage(imageURL, directory, status)
                    if (msg != lastMsg) {
                        activity?.runOnUiThread {
                            lastMsg = msg ?: ""
                            view?.let { msg?.let { it1 -> showSnackBar(it, it1) } }

                            savedImageDirectory = File(
                                directory.toString() + File.separator + getString(R.string.app_name) + File.separator + imageURL.substring(
                                    imageURL.lastIndexOf("/") + 1
                                )
                            )
                            fileName = imageURL.substring(
                                imageURL.lastIndexOf("/") + 1
                            )
                            Log.i("sss", "fileName: $fileName")

                        }

                    }
                    cursor.close()
                }
            }).start()


        } catch (e: Exception) {
            Log.e("sss", "Download Error: " + e.localizedMessage)
        }

    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> getString(R.string.photo_download_failed)
            DownloadManager.STATUS_PAUSED -> getString(R.string.photo_downlaod_paused)
            DownloadManager.STATUS_PENDING -> getString(R.string.photo_download_pending)
            DownloadManager.STATUS_RUNNING -> getString(R.string.photo_download_downloading)
            DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.photo_download_succesfull)
            else -> getString(R.string.photo_download_error)
        }
        return msg
    }


}