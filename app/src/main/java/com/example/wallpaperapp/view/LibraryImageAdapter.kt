package com.example.wallpaperapp.view

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import kotlinx.android.synthetic.main.item_library_images.view.*
import java.io.File

class LibraryImageAdapter(
    val context: Context?,
    private val imageList: ArrayList<File>?,
    val clickListener: (File?) -> Unit,
    val longClickListener: (File?) -> Unit

) : RecyclerView.Adapter<LibraryImageAdapter.ImageViewHolder>() {


    class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {



        fun bind(
            context: Context?,
            image: File?
        ) {
            if (context != null && image != null) {
                val bitmap = BitmapFactory.decodeFile(image.absolutePath)
                itemView.ivLibraryImage.setImageBitmap(bitmap)

            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_library_images, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        imageList?.get(position)?.let { holder.bind(context, it) }

        holder.itemView.setOnClickListener {
            clickListener(imageList?.get(position))
        }

/*        holder.itemView.(View.OnLongClickListener {
            longClickListener(imageList?.get(position))
            Toast.makeText(context, "Adapter", Toast.LENGTH_SHORT)

            true
        })*/


    }


    override fun getItemCount(): Int {
        return imageList?.size!!
    }


}