package com.pahadi.uncle.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.pahadi.uncle.domain.repositories.ProductRepository

class SearchViewModel : ViewModel() {



    fun searchProduct(query: String, district: String) =
        ProductRepository.searchProducts(query, district).cachedIn(viewModelScope)
}