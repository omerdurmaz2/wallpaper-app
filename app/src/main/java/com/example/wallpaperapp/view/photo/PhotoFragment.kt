package com.example.wallpaperapp.view.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import com.example.wallpaperapp.BuildConfig
import com.example.wallpaperapp.R
import com.example.wallpaperapp.R.color.bb_inActiveBottomBarItemColor
import com.example.wallpaperapp.view.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo.view.*
import java.io.File
import java.net.URL


class PhotoFragment : Fragment() {

    private var imageView: ImageView? = null
    private var lastDownloadStatus: Int? = null
    private lateinit var savedImageDirectory: File
    lateinit var btnBack: ImageButton
    lateinit var btnSetWallpaper: ImageButton
    lateinit var btnDownload: ImageButton

    private var fileName = ""
    val REQUEST_CODE = 2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_photo, container, false)
    }


    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        initViews(view)
        onBackPressed()
        bindImage()
        if (MainActivity.isLocal.get()!!) {
            view.btnDownlaod.visibility = View.GONE
        }
        setWallpaperClick(view)
        downloadClick()
        Handler().postDelayed({
            (activity as MainActivity).hideLoadingDialog()
        }, 500)
    }

    private fun setUI() {
        (context as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
    }


    private fun bindImage() {
        activity?.applicationContext?.let {
            imageView?.let { it1 ->
                if (MainActivity.isLocal.get()!!) Picasso.get()
                    .load(File(MainActivity.selectedImage.get()!!))
                    .into(it1)
                else Picasso.get().load(Uri.parse(MainActivity.selectedImage.get()!!)).into(it1)


            }
        }
    }

    private fun initViews(view: View) {
        imageView = view.findViewById(R.id.ivSelectedImage)
        btnBack = view.findViewById(R.id.fragmentPhotoBackButton)
        btnDownload = view.findViewById(R.id.btnDownlaod)
        btnSetWallpaper = view.findViewById(R.id.btnSetWallpaper)
    }

    private fun onBackPressed() {
        btnBack.setOnClickListener { activity?.onBackPressed() }
    }

    private fun setWallpaperClick(view: View) {

        btnSetWallpaper.setOnClickListener {
            setWallPaper()
            showSnackBar(view, getString(R.string.photo_wallpaper_set))

        }

    }

    private fun downloadClick() {
        btnDownload.setOnClickListener {
            MainActivity.selectedImage.get()?.let { it1 -> downloadImage(it1) }
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
                    FileProvider.getUriForFile(
                        it,
                        "com.example.wallpaperapp",
                        image
                    )
                }

            Log.i("sss", "path: $uriFile")


            val intent = Intent(Intent.ACTION_SET_WALLPAPER)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setDataAndType(uriFile, "image/*")
            intent.putExtra("mimeType", "image/*")
            startActivity(intent)
            /*val chooser = Intent.createChooser(intent, "set wallpaper")
            startActivity(chooser)*/


        } catch (e: Exception) {
            Log.e("sss", "error: ${e.localizedMessage}")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
    private fun downloadImage(imageURL: String, setWallpaper: Boolean? = false) {


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

            if (setWallpaper!!) {
                setWallPaper()
            }

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