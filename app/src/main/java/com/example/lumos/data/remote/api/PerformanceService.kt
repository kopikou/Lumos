package com.example.lumos.data.remote.api

import com.example.lumos.domain.entities.Performance
import com.example.lumos.domain.entities.PerformanceCreateUpdateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PerformanceService {
    @GET("api/performances/")
    suspend fun getPerformances(): List<Performance>

    @GET("api/performances/{id}/")
    suspend fun getPerformanceById(@Path("id") id: Int): Performance

    @POST("api/performances/")
    suspend fun createPerformance(@Body performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto

    @PUT("api/performances/{id}/")
    suspend fun updatePerformance(@Path("id") id: Int, @Body performance: PerformanceCreateUpdateDto): PerformanceCreateUpdateDto

    @DELETE("api/performances/{id}/")
    suspend fun deletePerformance(@Path("id") id: Int): Unit
}