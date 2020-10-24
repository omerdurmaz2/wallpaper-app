package com.example.wallpaperapp.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.example.wallpaperapp.R
import com.example.wallpaperapp.R.color.colorAlBiTerapiGreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.fragment_photo.view.*
import java.io.File
import java.lang.Exception


class PhotoFragment : Fragment() {

    private var imageView: ImageView? = null
    private var lastDownloadStatus: Int? = null
    private var savedImageDirectory = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUI()

        return inflater.inflate(R.layout.fragment_photo, container, false)
    }


    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.ivSelectedImage)

        activity?.applicationContext?.let {
            imageView?.let { it1 ->
                if (MainActivity.isLocal.get()!!) Picasso.get()
                    .load(File(MainActivity.selectedImage.get()!!))
                    .into(it1)
                else Picasso.get().load(Uri.parse(MainActivity.selectedImage.get()!!)).into(it1)


            }
        }

        if (MainActivity.isLocal.get()!!) {
            view.btnDownlaod.visibility = View.GONE
        }

        view.btnDownlaod.setOnClickListener {
            MainActivity.selectedImage.get()?.let { it1 -> downloadImage(it1) }
        }

        view.btnSetWallpaper.setOnClickListener {
            setWallpaper()
            showMessage(view, "Duvar Kağıdı Ayarlandı")
        }
    }

    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
    }


    private fun setWallpaper() {
        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                try {
                    val metrics = DisplayMetrics()
                    activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(bitmap)
                } catch (e: Exception) {
                    Log.i("sss", "Hata")
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.i("sss", "Hata")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.i("sss", "Hata")
            }

        }

        if (MainActivity.isLocal.get()!!) Picasso.get()
            .load(File(MainActivity.selectedImage.get()!!))
            .into(target)
        else Picasso.get().load(Uri.parse(MainActivity.selectedImage.get()!!)).into(target)

    }

    @SuppressLint("ResourceType")
    private fun showMessage(view: View, msg: String) {
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(resources.getInteger(colorAlBiTerapiGreen))
        snackbar.show()
    }


    var msg: String? = ""
    var lastMsg = ""
    private fun downloadImage(imageURL: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)
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
                    (getString(R.string.app_name) + "/" + imageURL.substring(imageURL.lastIndexOf("/") + 1))
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
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(imageURL, directory, status)
                if (msg != lastMsg) {
                    activity?.runOnUiThread {
                        lastMsg = msg ?: ""
                        view?.let { msg?.let { it1 -> showMessage(it, it1) } }

                        savedImageDirectory = "$directory" + File.separator + imageURL.substring(
                            imageURL.lastIndexOf("/") + 1
                        )
                    }

                }
                cursor.close()
            }
        }).start()

    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download Failed"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There is nothing to download"
        }
        return msg
    }


}