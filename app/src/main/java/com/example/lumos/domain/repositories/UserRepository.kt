package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.UserData
import com.example.lumos.domain.entities.UserUpdateRequest

interface UserRepository {
    suspend fun updateUser(userId: Int, user: UserUpdateRequest): UserData
}