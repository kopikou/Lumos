package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.TokenResponse
import retrofit2.Response

interface AuthRepository {
    suspend fun login(username: String, password: String): TokenResponse?
    suspend fun refreshToken(refreshToken: String): TokenResponse?
}