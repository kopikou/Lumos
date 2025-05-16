package com.example.lumos.data.remote.api

import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.EarningCreateUpdateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EarningService {
    @GET("api/earnings/")
    suspend fun getEarnings(): List<Earning>

    @GET("api/earnings/{id}/")
    suspend fun getEarningById(@Path("id") id: Int): Earning

    @POST("api/earnings/")
    suspend fun createEarning(@Body earning: EarningCreateUpdateDto): EarningCreateUpdateDto

    @PUT("api/earnings/{id}/")
    suspend fun updateEarning(@Path("id") id: Int, @Body earning: EarningCreateUpdateDto): EarningCreateUpdateDto

    @DELETE("api/earnings/{id}/")
    suspend fun deleteEarning(@Path("id") id: Int): Unit
}