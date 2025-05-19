package com.example.lumos.domain.usecases

import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.repository.AuthRepositoryImpl

class LoginUseCase(
    private val authRepository: AuthRepositoryImpl,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(username: String, password: String): Boolean {
        val response = authRepository.login(username, password)
        return if (response != null) {
            tokenManager.saveTokens(response.access, response.refresh)
            tokenManager.saveAdminStatus(response.user.isAdmin)
            tokenManager.saveUserNames(response.user.firstName, response.user.lastName)
            tokenManager.saveUserId(response.user.id)
            true
        } else {
            false
        }
    }
}