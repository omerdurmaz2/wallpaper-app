package com.example.wallpaperapp.view.search

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_search_category.view.*
import java.util.*

class SearchCategoryAdapter(
    val context: Context?,
    private val categories: Array<String>,
    private val category_colors: Array<String>,
    val clickListener: (Int?) -> Unit,
) : RecyclerView.Adapter<SearchCategoryAdapter.ImageViewHolder>() {


    class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            context: Context?,
            category: String,
        ) {
            if (context != null) {
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
        holder.bind(context, categories[position])
        holder.itemView.setBackgroundColor(Color.parseColor(category_colors[position]))
        holder.itemView.setOnClickListener {
            clickListener(position)
        }
    }



    override fun getItemCount(): Int {
        return categories.size
    }


}