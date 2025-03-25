package com.pahadi.uncle.domain.repositories

import com.pahadi.uncle.network.AboutUsService
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.network.utils.safelyCallApi

object AboutUsRepository {
    private val service = getRetrofitService(AboutUsService::class.java)

    suspend fun getAboutUs(): String {
        val res = service.getAboutUs()
        return res[0].asJsonObject["about_us"].asString
    }

    suspend fun getPrivacyPolicy(): String {
        val res = service.getPrivacyPolicy()
        return res[0].asJsonObject["privacy_policy"].asString
    }

    suspend fun getTermsAndConditions(): String {
        val res = service.getTermsAndCondition()
        return res[0].asJsonObject["terms_condition"].asString
    }

    suspend fun submitContactUs(email: String, title: String, description: String) = safelyCallApi {
        service.submitContactUs(
            description, email, title
        )
    }
}