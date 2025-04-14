package com.example.lumos.domain.services

import com.example.lumos.domain.entities.ShowRateCreateUpdateSerializer
import com.example.lumos.domain.entities.ShowRate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShowRateService {
    @GET("api/showrates/")
    suspend fun getShowRates(): List<ShowRate>

    @GET("api/showrates/{id}/")
    suspend fun getShowRateById(@Path("id") id: Int): ShowRate

    @POST("api/showrates/")
    suspend fun createShowRate(@Body showRate: ShowRateCreateUpdateSerializer): ShowRateCreateUpdateSerializer

    @PUT("api/showrates/{id}/")
    suspend fun updateShowRate(@Path("id") id: Int, @Body showRate: ShowRateCreateUpdateSerializer): ShowRateCreateUpdateSerializer

    @DELETE("api/showrates/{id}/")
    suspend fun deleteShowRate(@Path("id") id: Int): Unit
}