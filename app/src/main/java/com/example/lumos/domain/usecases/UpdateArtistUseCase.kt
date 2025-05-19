package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.domain.entities.Artist

class UpdateArtistUseCase(
    private val artistRepository: ArtistRepositoryImpl
) {
    suspend operator fun invoke(artistId: Int, updatedArtist: Artist): Artist {
        return artistRepository.updateArtist(artistId, updatedArtist)
    }
}