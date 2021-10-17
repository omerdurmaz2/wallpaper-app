package com.example.wallpaperapp.view.home

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.wallpaperapp.R
import com.example.wallpaperapp.model.ImageModel
import com.squareup.picasso.Picasso

class ImageListAdapter(
    val context: Context?,
    var list: ArrayList<ImageModel?>,
    val clickListener: (String?) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewItem = 1
    private val viewProg = 0

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = itemView.findViewById(R.id.ivItemHomeImage)
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }


    override fun getItemViewType(position: Int): Int {
        return if (list[position] != null) viewItem else viewProg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewItem) ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_home_images, parent, false)
        ) else LoadingViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_home_images_loading, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            val item = list[position]

            val circularProgress = context?.let { CircularProgressDrawable(it) }
            circularProgress?.strokeWidth = 5f
            circularProgress?.centerRadius = 30f
            circularProgress?.start()

            circularProgress?.let {
                Picasso.get().load(Uri.parse(item?.webformatURL)).placeholder(it)
                    .into(holder.image)
            }

            holder.itemView.setOnClickListener {
                clickListener(item?.largeImageURL)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}