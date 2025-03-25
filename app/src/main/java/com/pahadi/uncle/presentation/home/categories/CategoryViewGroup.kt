package com.pahadi.uncle.presentation.home.categories

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.get
import com.pahadi.uncle.R

class CategoryViewGroup @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(mContext, attrs, defStyle) {
    private val mLinearLayout: LinearLayout
    private var selectedCategoryIndex = 0
    var categoryClickedListener: OnCategoryClickedListener? = null

    init {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.category_view_group, this, true)
        mLinearLayout = view.findViewById(R.id.category_ll)
    }

    fun setCategories(categories: List<CategoryItem>) {
        mLinearLayout.removeAllViews()
        categories.forEachIndexed { index, category ->
            addCategoryView(category, index)
        }
        //selectIndex(0)
    }

    private fun addCategoryView(categoryItem: CategoryItem, index: Int) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 20
            marginEnd = 20
        }
        val categoryView = getCategoryView(categoryItem, index)
        mLinearLayout.addView(categoryView, layoutParams)
    }

    private fun getCategoryView(categoryItem: CategoryItem, tag: Int) =
        CategoryView(mContext).apply {
            setData(categoryItem)
            setTag(tag)
            setOnClickListener {
                val cv = it as CategoryView
                val previouslySelectedIndex = selectedCategoryIndex
                selectedCategoryIndex = cv.tag as Int
                val previouslySelectedView = mLinearLayout[previouslySelectedIndex] as CategoryView
                previouslySelectedView.setCategorySelected(false)
                cv.setCategorySelected(true)
                categoryClickedListener?.onCategoryClicked(categoryItem.id)
            }
        }

    fun selectIndex(index: Int) {
        mLinearLayout[index].performClick()
    }

    interface OnCategoryClickedListener {
        fun onCategoryClicked(categoryId: Int)
    }
}
