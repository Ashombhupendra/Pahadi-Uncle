package com.pahadi.uncle.presentation.product_details

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.pahadi.uncle.R

class ImageVideoAdapter(private val mediaList: List<Pair<MediaType, String>>, private val rvHost: rvHost) :
    RecyclerView.Adapter<ImageVideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVideoViewHolder {
        return when (viewType) {
            MediaType.IMAGE.ordinal -> ImageViewHolder.create(parent)
            else -> VideoViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: ImageVideoViewHolder, position: Int) {
        val media = mediaList[position]
        holder.bind(media.second, rvHost)




    }

    override fun getItemCount() = mediaList.size

    override fun getItemViewType(position: Int): Int {
        val media = mediaList[position]
        return media.first.ordinal
    }
}

