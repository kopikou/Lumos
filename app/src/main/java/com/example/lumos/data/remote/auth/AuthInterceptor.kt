package com.example.lumos.data.remote.auth

import com.example.lumos.data.local.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
//интерцептор для автоматической подстановки токена
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val accessToken = tokenManager.getAccessToken()
        if (accessToken != null) {
            request.addHeader("Authorization", "Bearer $accessToken")
        }
        return chain.proceed(request.build())
    }
}