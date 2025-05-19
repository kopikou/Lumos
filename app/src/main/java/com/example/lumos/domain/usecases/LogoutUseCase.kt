package com.example.lumos.domain.usecases

import com.example.lumos.data.local.auth.TokenManager

class LogoutUseCase(
    private val tokenManager: TokenManager
) {
    operator fun invoke() {
        tokenManager.clearTokens()
    }
}