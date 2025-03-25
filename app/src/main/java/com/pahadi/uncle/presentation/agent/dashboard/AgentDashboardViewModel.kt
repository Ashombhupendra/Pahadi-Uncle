package com.pahadi.uncle.presentation.agent.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.domain.repositories.AgentRepository
import com.pahadi.uncle.domain.repositories.AuthRepository
import kotlinx.coroutines.launch

class AgentDashboardViewModel : ViewModel() {
    fun getCounts(agentCode: String) = liveData {
        emit(
            AgentRepository.getAgentCounts(agentCode)
        )
    }






}
