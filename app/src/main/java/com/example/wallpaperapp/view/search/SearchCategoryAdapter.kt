package com.example.wallpaperapp.view.search

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.R

class SearchCategoryAdapter(
    val context: Context?,
    private val categories: Array<String>,
    private val category_colors: Array<String>,
    val clickListener: (Int?) -> Unit,
) : RecyclerView.Adapter<SearchCategoryAdapter.ImageViewHolder>() {


    class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val category: TextView = view.findViewById(R.id.tvSearchCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_search_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.category.text = categories[position]

        holder.itemView.setBackgroundColor(Color.parseColor(category_colors[position]))

        holder.itemView.setOnClickListener {
            clickListener(position)
        }
    }


    override fun getItemCount(): Int {
        return categories.size
    }


}