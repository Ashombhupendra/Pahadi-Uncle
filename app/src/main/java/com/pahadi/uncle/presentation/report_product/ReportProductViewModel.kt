package com.pahadi.uncle.presentation.report_product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.domain.repositories.ProductRepository
import kotlinx.coroutines.launch

class ReportProductViewModel(private val productId: String) : ViewModel() {
    val reportDescription = MutableLiveData("")
    val isLoading = MutableLiveData(false)
    val submittedSuccessfully = MutableLiveData<Boolean>(false)

    fun reportProduct() {
        viewModelScope.launch {
            isLoading.value = true
            ProductRepository.reportProduct(
                productId,
                reportDescription.value ?: "no description"
            )
            submittedSuccessfully.value = true
            isLoading.value = false
        }
    }
}


class ReportViewModelFactory(private val productId: String) :
    ViewModelProvider.NewInstanceFactory() {
//    c fun <T : ViewModel?> create(modelClass: Class<T>): T {
////        return ReportProductViewModel(productId) as T
////    }
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReportProductViewModel(productId) as T
    }
}