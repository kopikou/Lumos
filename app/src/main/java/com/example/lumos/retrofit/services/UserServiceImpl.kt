package com.example.lumos.retrofit.services

import android.util.Log
import com.example.lumos.domain.entities.UserData
import com.example.lumos.domain.entities.UserUpdateRequest
import com.example.lumos.domain.services.UserService
import com.example.lumos.retrofit.ApiClient

class UserServiceImpl : UserService {
    private val service = ApiClient.getUserService()

    override suspend fun updateUser(userId: Int, user: UserUpdateRequest): UserData {
        return try {
            service.updateUser(userId, user)
        } catch (e: Exception) {
            Log.e("UserService", "Error updating user", e)
            throw e
        }
    }
}