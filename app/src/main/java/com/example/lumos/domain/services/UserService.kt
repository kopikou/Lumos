package com.example.lumos.domain.services

import com.example.lumos.domain.entities.UserData
import com.example.lumos.domain.entities.UserUpdateRequest
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @PUT("api/artists/update-user/{user_id}/")
    suspend fun updateUser(
        @Path("user_id") userId: Int,
        @Body user: UserUpdateRequest
    ): UserData
}