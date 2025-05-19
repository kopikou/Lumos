package com.example.lumos.data.repository

import android.util.Log
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.ApiClient
import com.example.lumos.data.remote.impl.AuthServiceImpl
import com.example.lumos.domain.entities.TokenRequest
import com.example.lumos.domain.entities.TokenResponse
import com.example.lumos.domain.repositories.AuthRepository
import retrofit2.Response

//class AuthRepositoryImpl (
//    private val authService: AuthServiceImpl,
//    private val tokenManager: TokenManager
//) : AuthRepository {
//    override suspend fun login(username: String, password: String): TokenResponse? {
//        val response = authService.login(TokenRequest(username, password))
//        if (response.isSuccessful) {
//            response.body()?.also {
//                // Логирование для отладки
//                Log.d("AuthRepository", "Login success, isAdmin: ${it.user.isAdmin}")
//            }
//        } else {
//            Log.e("AuthRepository", "Login failed: ${response.errorBody()?.string()}")
//            null
//        }
//        if (response.body() != null) {
//            tokenManager.saveTokens(response.body()!!.access, response.body()!!.refresh)
//            tokenManager.saveAdminStatus(response.body()!!.user.isAdmin)
//            tokenManager.saveUserNames(
//                response.body()!!.user.firstName,
//                response.body()!!.user.lastName
//            )
//            tokenManager.saveUserId(response.body()!!.user.id)
//        }
//
//        return response.body()
//    }
//
//    override suspend fun refreshToken(refreshToken: String): TokenResponse? {
//        return authService.refreshToken(mapOf("refresh" to refreshToken)).body()
//    }
//
//}

class AuthRepositoryImpl(
    private val authService: AuthServiceImpl,
    private val tokenManager: TokenManager
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