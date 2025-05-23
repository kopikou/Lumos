package com.example.lumos.data.repository

import com.example.lumos.data.remote.impl.ArtistPerformanceServiceImpl
import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.ArtistPerformanceCreateUpdateDto
import com.example.lumos.domain.repositories.ArtistPerformanceRepository

class ArtistPerformanceRepositoryImpl (
    private val artistPerformanceService: ArtistPerformanceServiceImpl,
) : ArtistPerformanceRepository {
    override suspend fun getArtistPerformances(): List<ArtistPerformance> {
        return artistPerformanceService.getArtistPerformances()
    }

    override suspend fun getArtistPerformanceById(id: Int): ArtistPerformance {
        return artistPerformanceService.getArtistPerformanceById(id)
    }

    override suspend fun createArtistPerformance(artistPerformance: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto {
        return artistPerformanceService.createArtistPerformance(artistPerformance)
    }

    override suspend fun updateArtistPerformance(
        id: Int,
        artistPerformance: ArtistPerformanceCreateUpdateDto
    ): ArtistPerformanceCreateUpdateDto {
        return artistPerformanceService.updateArtistPerformance(id, artistPerformance)
    }

    override suspend fun deleteArtistPerformance(id: Int) {
        artistPerformanceService.deleteArtistPerformance(id)
    }
}