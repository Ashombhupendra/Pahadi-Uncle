package com.pahadi.uncle.domain.repositories

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.pahadi.uncle.domain.utils.API_URL
import com.pahadi.uncle.network.AgentService
import com.pahadi.uncle.network.data.AgentCounts
import com.pahadi.uncle.network.data.AgentUserDTO
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.network.utils.safelyCallApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

object AgentRepository {
    private val agentService = getRetrofitService(AgentService::class.java)

    suspend fun loginAgent(email: String, password: String, device_id : String) = withContext(Dispatchers.IO) {
        agentService.loginAgent(email, password, device_id)
    }

    suspend fun getAgentNOtification(agent_id : String) = safelyCallApi {
        agentService.getAgentNotification(agent_id)
    }

    suspend fun agentchangepass(userid : String, updatepassword: String)= safelyCallApi{
        agentService.changepassword(userid,updatepassword)
    }

    suspend fun getAgentCounts(agentCode: String): AgentCounts {
        val users = getCount("$API_URL/Webservice/agentusers/$agentCode")
        val points = getCount("$API_URL/Webservice/agentpoints/$agentCode")
        val todayRegister = getCount("$API_URL/Webservice/agenttodayregister/$agentCode")
        return AgentCounts(users, points, todayRegister)
    }

    private suspend fun getCount(url: String): String {
        return try {
            agentService.getCounts(url)["total_count"].asString
        } catch (ex: Exception) {
            "0"
        } catch (ex: HttpException) {
            "0"
        }catch (ex : UnknownHostException){
            "0"
        } catch (ex: java.lang.Exception) {
            "0"
        }catch (ex: JsonSyntaxException) {
            "0"
        }catch (ex : SocketTimeoutException){
            "0"
        }catch (ex : UnknownServiceException){
            "0"
        }catch (ex : IOException){
            "0"
        }
    }

}
