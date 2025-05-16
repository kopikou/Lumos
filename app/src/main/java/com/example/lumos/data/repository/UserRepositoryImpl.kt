package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.UserServiceImpl
import com.example.lumos.domain.entities.UserData
import com.example.lumos.domain.entities.UserUpdateRequest
import com.example.lumos.domain.repositories.UserRepository

class UserRepositoryImpl (
    private val userService: UserServiceImpl
) : UserRepository {
    override suspend fun updateUser(userId: Int, user: UserUpdateRequest): UserData {
        return userService.updateUser(userId, user)
    }
}