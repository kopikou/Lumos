package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl

class GetArtistIdUseCase(
    private val artistRepository: ArtistRepositoryImpl
) {
    suspend operator fun invoke(firstName: String, lastName: String): Int {
        return artistRepository.getArtistByName(firstName, lastName).id
    }
}