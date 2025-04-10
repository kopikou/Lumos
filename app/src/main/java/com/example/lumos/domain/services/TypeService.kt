package com.example.lumos.domain.services

import com.example.lumos.domain.entities.Type
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TypeService {
    @GET("api/types/")
    suspend fun getTypes(): List<Type>

    @GET("api/types/{id}/")
    suspend fun getTypeById(@Path("id") id: Int): Type

    @POST("api/types/")
    suspend fun createType(@Body type: Type): Type

    @PUT("api/types/{id}/")
    suspend fun updateType(@Path("id") id: Int, @Body type: Type): Type

    @DELETE("api/types/{id}/")
    suspend fun deleteType(@Path("id") id: Int): Unit
}