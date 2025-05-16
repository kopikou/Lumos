package com.example.lumos.domain.repositories

import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.ArtistPerformanceCreateUpdateDto

interface ArtistPerformanceRepository {
    suspend fun getArtistPerformances(): List<ArtistPerformance>
    suspend fun getArtistPerformanceById(id: Int): ArtistPerformance
    suspend fun createArtistPerformance(artistPerformance: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto
    suspend fun updateArtistPerformance(id: Int, artistPerformance: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto
    suspend fun deleteArtistPerformance(id: Int)
}