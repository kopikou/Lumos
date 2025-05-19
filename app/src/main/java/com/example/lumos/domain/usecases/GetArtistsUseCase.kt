package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.domain.entities.Artist

class GetArtistsUseCase(
    private val artistRepository: ArtistRepositoryImpl
) {
    suspend operator fun invoke(): List<Artist> {
        return artistRepository.getArtists()
    }
}