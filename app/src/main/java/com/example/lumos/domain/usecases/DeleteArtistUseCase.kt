package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl

class DeleteArtistUseCase(private val artistRepository: ArtistRepositoryImpl) {
    suspend operator fun invoke(artistId: Int) {
        artistRepository.deleteArtist(artistId)
    }
}