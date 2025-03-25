package com.pahadi.uncle.network.utils

import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Handshake
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

/**
 * Encapsulates different Exception Catch blocks.
 *
 * A general purpose function that takes a functional block, in which the api call is made.
 * It wraps the result from the call in one of the sub classes of the sealed super class called ResultWrapper.
 */
suspend fun <T> safelyCallApi(apiCall: suspend () -> T): ResultWrapper<out T> {
    return withContext(Dispatchers.IO) {
        try {
            val result = apiCall()

            ResultWrapper.Success(result)
        } catch (throwable: Throwable) {
            val message = when (throwable) {

                is IOException,
                    is UnknownServiceException -> "Failed to load"
                is SocketTimeoutException ->"Error Connecting to server."
                is UnknownHostException -> "Error Connecting to server."
                is HttpException ->
                    when (throwable.code()) {
                        408 -> "Request Timed Out.\nPlease Check your internet Connection"
                        400 -> "Request Failed, Please check all the entries"
                        500 -> "A Server Error Occurred.\nTry restarting app.\nIf problem persists contact us via email."
                        504 -> "Not connected to Internet"
                        else -> "An Unexpected Error occurred while completing request."+throwable.message
                    }
                else -> "An Unexpected Error occurred."+throwable.message
            }
            ResultWrapper.Failure(message)
        }
    }
}
