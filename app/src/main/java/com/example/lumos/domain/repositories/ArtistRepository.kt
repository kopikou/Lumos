package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.Artist

interface ArtistRepository {
    suspend fun getArtists(): List<Artist>
    suspend fun getArtistById(id: Int): Artist
    suspend fun getArtistByName(firstName: String, lastName: String): Artist
    suspend fun updateArtist(id: Int, artist: Artist): Artist
    suspend fun createArtist(artist: Artist): Artist
    suspend fun deleteArtist(id: Int)
}