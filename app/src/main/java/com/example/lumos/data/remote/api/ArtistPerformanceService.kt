package com.example.lumos.data.remote.api

import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.ArtistPerformanceCreateUpdateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ArtistPerformanceService {
    @GET("api/artistperformances/")
    suspend fun getArtistPerformances(): List<ArtistPerformance>

    @GET("api/artistperformances/{id}/")
    suspend fun getArtistPerformanceById(@Path("id") id: Int): ArtistPerformance

    @POST("api/artistperformances/")
    suspend fun createArtistPerformance(@Body artistPerformance: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto

    @PUT("api/artistperformances/{id}/")
    suspend fun updateArtistPerformance(@Path("id") id: Int, @Body artistPerformance: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto

    @DELETE("api/artistperformances/{id}/")
    suspend fun deleteArtistPerformance(@Path("id") id: Int): Unit
}