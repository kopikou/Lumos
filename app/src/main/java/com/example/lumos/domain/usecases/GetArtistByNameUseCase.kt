package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.domain.entities.Artist

class GetArtistByNameUseCase(
    private val artistRepository: ArtistRepositoryImpl
) {
    suspend operator fun invoke(firstName: String, lastName: String): Artist {
        return artistRepository.getArtistByName(firstName, lastName)
    }
}