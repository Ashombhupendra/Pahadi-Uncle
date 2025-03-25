package com.pahadi.uncle.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel(){

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    val building = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val landmark = MutableLiveData<String>()
    val pincode = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val district = MutableLiveData<String>()







}