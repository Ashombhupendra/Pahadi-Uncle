package com.pahadi.uncle.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class OnlineCachingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val maxAge = 2 // read from cache for 2 seconds even if there is internet connection
        return response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .removeHeader("Pragma")
            .build()
    }
}
