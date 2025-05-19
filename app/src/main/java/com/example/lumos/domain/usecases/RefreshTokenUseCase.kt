package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.AuthRepositoryImpl
import com.example.lumos.domain.entities.TokenResponse

class RefreshTokenUseCase(
    private val authRepository: AuthRepositoryImpl
) {
    suspend operator fun invoke(refreshToken: String): TokenResponse? {
        return authRepository.refreshToken(refreshToken)
    }
}