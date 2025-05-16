package com.example.lumos.data.remote.impl

import com.example.lumos.data.remote.ApiClient
import com.example.lumos.data.remote.api.AuthService
import com.example.lumos.domain.entities.TokenRequest
import com.example.lumos.domain.entities.TokenResponse
import retrofit2.Response

class AuthServiceImpl: AuthService {
    private val service = ApiClient.getAuthService()
    override suspend fun login(request: TokenRequest): Response<TokenResponse> {
        return service.login(request)
    }

    override suspend fun refreshToken(request: Map<String, String>): Response<TokenResponse> {
        return service.refreshToken(request)
    }
}