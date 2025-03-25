package com.pahadi.uncle.network.utils

import android.icu.util.TimeUnit
import com.google.android.material.timepicker.TimeFormat
//import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.utils.API_URL
import com.pahadi.uncle.network.interceptors.ApiKeyInterceptor
import com.pahadi.uncle.network.interceptors.OfflineCachingInterceptor
import com.pahadi.uncle.network.interceptors.OnlineCachingInterceptor
import okhttp3.Cache
import okhttp3.ConnectionSpec
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


private fun getCache(): Cache {
    val cacheSize = (10 * 1024 * 1024).toLong()
    val cacheDir = PahadiUncleApplication.instance.cacheDir
    return Cache(cacheDir, cacheSize)
}

private fun getOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    //for performing request in parallel
    val dispatcher = Dispatcher().apply {
        maxRequests = 3
    }
    return OkHttpClient.Builder()
        .cache(getCache())
        .dispatcher(dispatcher)
        .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
//        .addInterceptor(OkHttpProfilerInterceptor())
        .addInterceptor(loggingInterceptor)
        .connectTimeout(500, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(500, java.util.concurrent.TimeUnit.SECONDS)
        .addNetworkInterceptor(OnlineCachingInterceptor())
        .addInterceptor(OfflineCachingInterceptor())
        .addInterceptor(ApiKeyInterceptor())
        .build()
}

private fun getRetrofitBuilder(): Retrofit.Builder {
    return Retrofit.Builder().baseUrl(API_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
}

fun <T> getRetrofitService(clazz: Class<T>): T {
    return getRetrofitBuilder().build().create(clazz)
}
