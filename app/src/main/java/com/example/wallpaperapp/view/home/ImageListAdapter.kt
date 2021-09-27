package com.example.wallpaperapp.view.home

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.squareup.picasso.Picasso

class ImageListAdapter(
    val context: Context?,
    ivList: List<ImageModel>?,
    val clickListener: (String?) -> Unit

) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    private var imageList: List<ImageModel>? = ivList

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = itemView.findViewById(R.id.ivItemHomeImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_home_images, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        Picasso.get().load(Uri.parse(imageList?.get(position)?.webformatURL)).into(holder.image)

        holder.itemView.setOnClickListener {
            clickListener(imageList?.get(position)?.largeImageURL)
        }
    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }
}