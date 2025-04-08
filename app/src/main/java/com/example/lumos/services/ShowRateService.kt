package com.example.lumos.services

import com.example.lumos.entities.ShowRate
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
    suspend fun createShowRate(@Body showRate: ShowRate): ShowRate

    @PUT("api/showrates/{id}/")
    suspend fun updateShowRate(@Path("id") id: Int, @Body showRate: ShowRate): ShowRate

    @DELETE("api/showrates/{id}/")
    suspend fun deleteShowRate(@Path("id") id: Int): Unit
}