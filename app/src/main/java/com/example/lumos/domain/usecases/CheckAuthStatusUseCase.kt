package com.example.lumos.domain.usecases

import com.example.lumos.data.local.auth.TokenManager

class CheckAuthStatusUseCase(
    private val tokenManager: TokenManager
) {
    operator fun invoke(): Boolean {
        return tokenManager.getAccessToken() != null
    }
}