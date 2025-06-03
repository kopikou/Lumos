package com.example.lumos.data.remote.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.ArtistPerformanceCreateUpdateDto
import com.example.lumos.data.remote.api.ArtistPerformanceService
import com.example.lumos.data.remote.ApiClient

class ArtistPerformanceServiceImpl: ArtistPerformanceService {
    val service = ApiClient.getArtistPerformanceService()
    override suspend fun getArtistPerformances(): List<ArtistPerformance> {
        lateinit var artistPerformances: List<ArtistPerformance>
        try {
            artistPerformances = service.getArtistPerformances()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки артистов и номеров", e)
        }
        return artistPerformances
    }

    override suspend fun getArtistPerformanceById(id: Int): ArtistPerformance {
        lateinit var artistPerformance: ArtistPerformance
        try {
            artistPerformance = service.getArtistPerformanceById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки артиста и номера", e)
        }
        return artistPerformance
    }

    override suspend fun createArtistPerformance(_artistPerformance: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto {
        lateinit var artistPerformance: ArtistPerformanceCreateUpdateDto
        try {
            artistPerformance = service.createArtistPerformance(_artistPerformance)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания артиста и номера", e)
        }
        return artistPerformance
    }

    override suspend fun updateArtistPerformance(
        id: Int,
        _artistPerformance: ArtistPerformanceCreateUpdateDto
    ): ArtistPerformanceCreateUpdateDto {
        lateinit var artistPerformance: ArtistPerformanceCreateUpdateDto
        try {
            artistPerformance = service.updateArtistPerformance(id,_artistPerformance)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обновления артиста и номера", e)
        }
        return artistPerformance
    }

    override suspend fun deleteArtistPerformance(id: Int) {
        try {
            service.deleteArtistPerformance(id)
        } catch (e: Exception) {
            null
        }
    }
}