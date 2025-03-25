package com.pahadi.uncle.presentation.agent.login

import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.*
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.repositories.AgentRepository
import com.pahadi.uncle.domain.utils.AgentLoginStatus
import com.pahadi.uncle.domain.utils.ERROR_MSG
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.contact_us.Field
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AgentLoginViewModel : ViewModel() {
    val loginState = MutableLiveData<LoginState>()
    val token = MutableLiveData<String>()

    val email = Field {
        when {
            it.isNullOrBlank() -> "Email Cannot be blank"
            !it.matches(Patterns.EMAIL_ADDRESS.toRegex()) -> "Enter a Valid Email"
            else -> null
        }
    }

    val password = Field {
        when {
            it.isNullOrBlank() -> "Please enter a password"
            it.length < 8 -> "Password should contain at least 8 chars"
            else -> null
        }
    }

    val isFormValid = MediatorLiveData<Boolean>().apply {
        var (emailValid, passwordValid) = false to false

        fun computeValue() {
            this.value = emailValid && passwordValid
        }

        addSource(email.errorMessage) {
            emailValid = it == null
            computeValue()
        }
        addSource(password.errorMessage) {
            passwordValid = it == null
            computeValue()
        }
    }

    fun loginAgent() {
        if (!PahadiUncleApplication.instance.isConnectedToInternet()){
            Toast.makeText(PahadiUncleApplication.instance,"No Internet Connection !", Toast.LENGTH_SHORT).show()
        }else{

            loginState.value = LoginState.Loading
        viewModelScope.launch {
            loginState.value = try {
                val res = AgentRepository.loginAgent(email.value.value!!, password.value.value!!, token.value.toString())
                SharedPrefHelper.agentLoginStatus = AgentLoginStatus.LoggedIn(res.agentDto)
                LoginState.Success
            } catch (ex: HttpException) {
                if (ex.code() == 400) {
                    LoginState.Failed("Bad Credentials")
                } else {
                    LoginState.Failed(ex.localizedMessage ?: ERROR_MSG)
                }
            }
        }
    }
    }
}
