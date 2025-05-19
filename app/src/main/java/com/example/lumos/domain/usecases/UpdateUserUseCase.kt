package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.UserRepositoryImpl
import com.example.lumos.domain.entities.UserData
import com.example.lumos.domain.entities.UserUpdateRequest

class UpdateUserUseCase(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(userId: Int, userUpdateRequest: UserUpdateRequest): UserData {
        return userRepository.updateUser(userId, userUpdateRequest)
    }
}