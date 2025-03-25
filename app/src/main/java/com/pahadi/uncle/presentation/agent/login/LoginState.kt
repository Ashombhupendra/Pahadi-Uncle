package com.pahadi.uncle.presentation.agent.login

sealed class LoginState {
    object Loading : LoginState()
    data class Failed(val reason: String) : LoginState()
    object Success : LoginState()
}
