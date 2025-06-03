package com.example.lumos.data.remote.impl

import android.util.Log
import com.example.lumos.domain.entities.UserData
import com.example.lumos.domain.entities.UserUpdateRequest
import com.example.lumos.data.remote.api.UserService
import com.example.lumos.data.remote.ApiClient

class UserServiceImpl : UserService {
    private val service = ApiClient.getUserService()

    override suspend fun updateUser(userId: Int, user: UserUpdateRequest): UserData {
        return try {
            service.updateUser(userId, user)
        } catch (e: Exception) {
            Log.e("UserService", "Ошибка обновления пользователя", e)
            throw e
        }
    }
}