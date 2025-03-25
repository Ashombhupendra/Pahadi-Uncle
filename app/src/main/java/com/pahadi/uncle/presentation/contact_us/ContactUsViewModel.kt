package com.pahadi.uncle.presentation.contact_us

import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.*
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.AboutUsRepository
import kotlinx.coroutines.launch

class ContactUsViewModel : ViewModel() {
    val email = Field {
        when {
            it.isNullOrBlank() -> "Email is Mandatory"
            !it.matches(Patterns.EMAIL_ADDRESS.toRegex()) -> "Enter a Valid Email"
            else -> null
        }
    }

    val title = Field {
        when {
            it.isNullOrBlank() -> "Title is mandatory"
            it.length > 250 -> "Title can only be 250 characters long"
            else -> null
        }
    }

    val description = Field {
        when {
            it.isNullOrBlank() -> "Description is Mandatory"
            it.length > 500 -> "Description can only have 500 characters"
            else -> null
        }
    }

    val formIsValid = MediatorLiveData<Boolean>().apply {
        var (emailValid, titleValid, descriptionValid) = Triple(false, false, false)

        fun computeValue() {
            this.value = emailValid && titleValid && descriptionValid
        }

        addSource(email.errorMessage) {
            emailValid = it == null
            computeValue()
        }
        addSource(title.errorMessage) {
            titleValid = it == null
            computeValue()
        }
        addSource(description.errorMessage) {
            descriptionValid = it == null
            computeValue()
        }
    }

    private val _submitOperationState = MutableLiveData<NetworkOperationState>()
    val submitOperationState: LiveData<NetworkOperationState> = _submitOperationState

    fun submit() {
        if (formIsValid.value != true) {
            Toast.makeText(
                PahadiUncleApplication.instance,
                "Please fill all the required fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        } //only proceeding if the form is valid

        viewModelScope.launch {
            _submitOperationState.value = NetworkOperationState.Loading
            val res = AboutUsRepository.submitContactUs(
                email = email.value.value!!,
                title = title.value.value!!,
                description = description.value.value!!
            )
            when (res) {
                is ResultWrapper.Success -> _submitOperationState.value =
                    NetworkOperationState.Success
                is ResultWrapper.Failure -> _submitOperationState.value =
                    NetworkOperationState.Error(res.errorMessage)
            }
        }
    }
}


class Field(validation: (newValue: String?) -> String?) {

    // LiveData for the actual value
    val value = MutableLiveData<String>()

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        // Manually observe the value and apply validation
        value.observeForever(Observer { newValue ->
            _errorMessage.value = validation(newValue)
        })
    }
}

sealed class NetworkOperationState {
    object Loading : NetworkOperationState()
    object Success : NetworkOperationState()
    data class Error(val errorMessage: String) : NetworkOperationState()
}