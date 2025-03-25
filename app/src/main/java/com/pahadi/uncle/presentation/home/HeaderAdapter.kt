package com.pahadi.uncle.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.R
import com.pahadi.uncle.presentation.home.categories.CategoryItem
import com.pahadi.uncle.presentation.home.categories.CategoryViewGroup

class HeaderAdapter(
    private val onCategoryClickedListener: CategoryViewGroup.OnCategoryClickedListener,
    private val onSearchClicked: () -> Unit
) :
    RecyclerView.Adapter<HeaderViewHolder>() {
    var viewCached: HeaderViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_screen_header, parent, false)
        return HeaderViewHolder(view).also { viewCached = it }
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.categoryViewGroup.categoryClickedListener = onCategoryClickedListener
        holder.searchEt.setOnClickListener {
            onSearchClicked()
        }
    }

    override fun getItemCount() = 1

    fun setCategories(categories: List<CategoryItem>) {
        viewCached?.categoryViewGroup?.let {
            it.setCategories(categories)
            it.selectIndex(0)
        }
    }
}

class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val categoryViewGroup: CategoryViewGroup = view.findViewById(R.id.category_rv)
    val searchEt: EditText = view.findViewById(R.id.search_et)
}

