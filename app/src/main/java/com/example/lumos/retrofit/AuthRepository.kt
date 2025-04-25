package com.example.lumos.retrofit

import com.example.lumos.domain.entities.TokenRequest
import com.example.lumos.domain.entities.TokenResponse

class AuthRepository {
    suspend fun login(username: String, password: String): TokenResponse? {
        return try {
            val response = ApiClient.getAuthService().login(TokenRequest(username, password))
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun refreshToken(refreshToken: String): TokenResponse? {
        return try {
            val response = ApiClient.getAuthService().refreshToken(mapOf("refresh" to refreshToken))
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}