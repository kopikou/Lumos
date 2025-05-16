package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.repositories.ArtistRepository

class ArtistRepositoryImpl (
    private val artistService: ArtistServiceImpl
) : ArtistRepository {

    override suspend fun getArtists(): List<Artist> {
        return artistService.getArtists()
    }

    override suspend fun getArtistById(id: Int): Artist {
        return artistService.getArtistById(id)
    }

    override suspend fun getArtistByName(firstName: String, lastName: String): Artist {
        return artistService.getArtistByName(firstName, lastName)
    }

    override suspend fun updateArtist(id: Int, artist: Artist): Artist {
        return artistService.updateArtist(id, artist)
    }

    override suspend fun createArtist(artist: Artist): Artist {
        return artistService.createArtist(artist)
    }

    override suspend fun deleteArtist(id: Int) {
        artistService.deleteArtist(id)
    }
}