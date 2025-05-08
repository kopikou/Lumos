package com.example.lumos.retrofit.authentification

import android.util.Log
import com.example.lumos.domain.entities.TokenRequest
import com.example.lumos.domain.entities.TokenResponse
import com.example.lumos.retrofit.ApiClient


class AuthRepository {
    suspend fun login(username: String, password: String): TokenResponse? {
        return try {
            val response = ApiClient.getAuthService().login(TokenRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.also {
                    // Логирование для отладки
                    Log.d("AuthRepository", "Login success, isAdmin: ${it.user.isAdmin}")
                }
            } else {
                Log.e("AuthRepository", "Login failed: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
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