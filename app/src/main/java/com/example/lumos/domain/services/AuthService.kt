package com.example.lumos.domain.services

import com.example.lumos.domain.entities.TokenRequest
import com.example.lumos.domain.entities.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/token/")
    suspend fun login(@Body request: TokenRequest): Response<TokenResponse>

    @POST("api/token/refresh/")
    suspend fun refreshToken(@Body request: Map<String, String>): Response<TokenResponse>
}