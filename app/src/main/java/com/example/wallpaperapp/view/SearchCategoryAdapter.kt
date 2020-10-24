package com.example.wallpaperapp.view

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_images.view.*
import kotlinx.android.synthetic.main.item_library_images.view.*
import kotlinx.android.synthetic.main.item_search_category.view.*
import java.io.File

class SearchCategoryAdapter(
    val context: Context?,
    private val categories: Array<String>,
    private val categoryImages: Array<String>,
    val clickListener: (String?) -> Unit,
) : RecyclerView.Adapter<SearchCategoryAdapter.ImageViewHolder>() {


    class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            context: Context?,
            category: String,
            image: String?
        ) {
            if (context != null) {
                Log.i("sss", "image: $image")
                Picasso.get().load(Uri.parse(image)).into(itemView.ivSearchCategory)
                itemView.tvSearchCategory.text = category
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_search_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(context, categories[position], categoryImages[position])

        holder.itemView.setOnClickListener {
            clickListener(categories[position].toLowerCase())
        }
    }


    override fun getItemCount(): Int {
        return categories.size
    }


}