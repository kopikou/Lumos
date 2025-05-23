package com.example.lumos.data.repository

import android.util.Log
import com.example.lumos.data.remote.impl.AuthServiceImpl
import com.example.lumos.domain.entities.TokenRequest
import com.example.lumos.domain.entities.TokenResponse
import com.example.lumos.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val authService: AuthServiceImpl
) : AuthRepository {
    override suspend fun login(username: String, password: String): TokenResponse? {
        return try {
            val response = authService.login(TokenRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.also {
                    Log.d("AuthRepository", "Login success, isAdmin: ${it.user.isAdmin}")
                }
            } else {
                Log.e("AuthRepository", "Login failed: ${response.errorBody()?.string()}")
                null
            }
            response.body()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
            null
        }
    }

    override suspend fun refreshToken(refreshToken: String): TokenResponse? {
        return try {
            authService.refreshToken(mapOf("refresh" to refreshToken)).body()
        } catch (e: Exception) {
            null
        }
    }
}