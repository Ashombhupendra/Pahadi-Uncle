package com.pahadi.uncle

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkConnection (private val context: Context) : LiveData<Boolean>(){
    private var coonectivityManager :
            ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkcallback  : ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        updateConnection()
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                coonectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP->{
                lollipopnetworkRequest()
            }else ->{
            context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            coonectivityManager.unregisterNetworkCallback(connectivityManagerCallback())

        }else{
            context.unregisterReceiver(networkReceiver)
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopnetworkRequest(){
        val requestBuilder =NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        coonectivityManager.registerNetworkCallback(
            requestBuilder.build()
         , connectivityManagerCallback()
        )
    }

    private fun connectivityManagerCallback() : ConnectivityManager.NetworkCallback{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            networkcallback = object : ConnectivityManager.NetworkCallback(){
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(true)
                }

            }
            return networkcallback
        }else{
            throw IllegalAccessError("Error")
        }
    }
    private val networkReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }
    private fun updateConnection(){
        val activenetwork : NetworkInfo? = coonectivityManager.activeNetworkInfo
        postValue(activenetwork?.isConnected == true)
    }
}