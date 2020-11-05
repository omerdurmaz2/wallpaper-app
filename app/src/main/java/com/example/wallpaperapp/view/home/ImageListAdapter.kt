package com.example.wallpaperapp.view.home

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_images.view.*

class ImageListAdapter(
    val context: Context?,
    ivList: List<ImageModel>?,
    val clickListener: (String?) -> Unit

) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    private var imageList: List<ImageModel>? = ivList

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            context: Context?,
            image: ImageModel
        ) {
            if (context != null) {
                Picasso.get().load(Uri.parse(image.webformatURL)).into(itemView.ivItemHomeImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_home_images, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        imageList?.get(position)?.let { holder.bind(context, it) }
        holder.itemView.setOnClickListener {
            clickListener(imageList?.get(position)?.largeImageURL)
        }
    }

    override fun getItemCount(): Int {
        return imageList?.size!!
    }
}