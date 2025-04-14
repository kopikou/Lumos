package com.example.lumos.domain.services

import com.example.lumos.domain.entities.Artist
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ArtistService {
    @GET("api/artists/")
    suspend fun getArtists(): List<Artist>

    @GET("api/artists/{id}/")
    suspend fun getArtistById(@Path("id") id: Int): Artist

    @POST("api/artists/")
    suspend fun createArtist(@Body artist: Artist): Artist

    @PUT("api/artists/{id}/")
    suspend fun updateArtist(@Path("id") id: Int, @Body artist: Artist): Artist

    @DELETE("api/artists/{id}/")
    suspend fun deleteArtist(@Path("id") id: Int): Unit
}