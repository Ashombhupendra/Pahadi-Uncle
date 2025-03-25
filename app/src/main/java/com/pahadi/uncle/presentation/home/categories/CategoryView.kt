package com.pahadi.uncle.presentation.home.categories

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.pahadi.uncle.databinding.ItemCategoryBinding

class CategoryView @JvmOverloads constructor(
    private val mContext: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(mContext, attr, defStyle) {
    private val mBinding: ItemCategoryBinding

    init {
        this.orientation = VERTICAL
        val layoutInflater = LayoutInflater.from(mContext)
        mBinding = ItemCategoryBinding.inflate(layoutInflater, this, true)
    }

    fun setData(data: CategoryItem){
        mBinding.categoryItem = data
    }

    fun setCategorySelected(selected:Boolean){
        mBinding.selected = selected
    }
}

