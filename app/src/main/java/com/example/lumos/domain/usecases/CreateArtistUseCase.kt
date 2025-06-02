package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.domain.entities.Artist

class CreateArtistUseCase(private val artistRepository: ArtistRepositoryImpl) {
    suspend operator fun invoke(artist: Artist): Artist {
        return artistRepository.createArtist(artist)
    }
}