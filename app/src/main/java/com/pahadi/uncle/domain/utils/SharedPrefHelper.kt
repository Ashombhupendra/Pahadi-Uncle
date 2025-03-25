package com.pahadi.uncle.domain.utils

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.data.UserEntity
import com.pahadi.uncle.network.data.AgentDto
import com.pahadi.uncle.network.data.UserDto
import com.pahadi.uncle.presentation.notifications.NotificationService
import java.lang.Exception

object SharedPrefHelper {
    private const val LOGIN_STATUS = "LOGIN_STATUS"
    private const val USER = "USER"
    private const val AGENT_LOGIN_STATUS = "agent_login"
    private const val AGENT_DTO = "agent dto"
    private const val PRODUCT_AVAILABLE_STATUS = "productavailablestatus"
    private const val NOTIFICATION_ALERT = "NOTIFICATION_ALERT"
    private val sharedPrefs: SharedPreferences

    init {
        val context = PahadiUncleApplication.instance
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    }
    var adapterpostion : Int
          get() {
              return  sharedPrefs.getInt("position", -1)
          }
          set(value) {
              sharedPrefs.edit {
                  putInt("position", value)
              }
          }

   fun setNotificationAlert(an : String){
       sharedPrefs.edit {
           putString(NOTIFICATION_ALERT, an)
       }
   }
    fun getNotificationAlert() : String{
               return sharedPrefs.getString(NOTIFICATION_ALERT,"").toString()
    }
    var stoken: String?
        get() {
            return NotificationService.sharedPref?.getString("token", "")
        }
        set(value) {
            NotificationService.sharedPref?.edit()?.putString("token", value)?.apply()
        }

    var productavailablestatus : String?
              get() {
                  return sharedPrefs.getString(PRODUCT_AVAILABLE_STATUS, "0")
              }
       set(value) {
                 sharedPrefs.edit{
                   putString(PRODUCT_AVAILABLE_STATUS,value)
            }
       }

    var isLoggedIn: Boolean
        get() {
            return sharedPrefs.getBoolean(LOGIN_STATUS, false)
        }
        set(value) {
            sharedPrefs.edit {
                putBoolean(LOGIN_STATUS, value)
            }
        }

    var user: UserEntity
        set(value) = sharedPrefs.edit {
            val userJson = Gson().toJson(value)
            putString(USER, userJson)
            Log.d("Users_", userJson.toString())
        }
        get() {
            val userString = sharedPrefs.getString(USER, null) ?: throw Exception("User not Found")
            Log.d("Users_", userString)



            return  Gson().fromJson(userString, UserEntity::class.java)
        }

    var agentLoginStatus: AgentLoginStatus
        get() {
            val isLoggedIn = sharedPrefs.getBoolean(AGENT_LOGIN_STATUS, false)
            if (isLoggedIn) {
                val agentDtoString = sharedPrefs.getString(AGENT_DTO, null)
                return if (agentDtoString == null)
                    AgentLoginStatus.NotLoggedIn
                else {
                    try {
                        val agentDto =
                            Gson().fromJson<AgentDto>(agentDtoString, AgentDto::class.java)
                        AgentLoginStatus.LoggedIn(agentDto)
                    } catch (ex: Exception) {
                        AgentLoginStatus.NotLoggedIn
                    }
                }
            } else {
                return AgentLoginStatus.NotLoggedIn
            }
        }
        set(value) {
            sharedPrefs.edit {
                if (value is AgentLoginStatus.NotLoggedIn) {
                    putBoolean(AGENT_LOGIN_STATUS, false)
                } else if (value is AgentLoginStatus.LoggedIn) {
                    putBoolean(AGENT_LOGIN_STATUS, true)
                    val agentDtoString = Gson().toJson(value.agentDto)
                    putString(AGENT_DTO, agentDtoString)
                }
            }
        }
}


sealed class AgentLoginStatus {
    object NotLoggedIn : AgentLoginStatus()
    data class LoggedIn(val agentDto: AgentDto) : AgentLoginStatus()
}
