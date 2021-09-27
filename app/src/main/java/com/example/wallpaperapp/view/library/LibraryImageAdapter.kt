package com.example.wallpaperapp.view.library

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import java.io.File

class LibraryImageAdapter(
    val context: Context?,
    private val imageList: ArrayList<File>?,
    val clickListener: (File?) -> Unit,
    val longClickListener: (File?) -> Unit

) : RecyclerView.Adapter<LibraryImageAdapter.ImageViewHolder>() {


    class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivLibraryImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_library_images, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.image.setImageBitmap(BitmapFactory.decodeFile(imageList?.get(position)?.absolutePath))

        holder.itemView.setOnClickListener {
            clickListener(imageList?.get(position))
        }

        holder.itemView.setOnLongClickListener {
            true
        }
    }


    override fun getItemCount(): Int {
        return imageList?.size?:0
    }


}