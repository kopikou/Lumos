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
            Log.d(TAG, "Received artistPerformances: $artistPerformances")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching artistPerformances", e)
        }
        return artistPerformances
    }

    override suspend fun getArtistPerformanceById(id: Int): ArtistPerformance {
        lateinit var artistPerformance: ArtistPerformance
        try {
            artistPerformance = service.getArtistPerformanceById(id)
            Log.d(TAG, "Received artistPerformance: $artistPerformance")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching artistPerformance", e)
        }
        return artistPerformance
    }

    override suspend fun createArtistPerformance(_artistPerformance: ArtistPerformanceCreateUpdateDto): ArtistPerformanceCreateUpdateDto {
        lateinit var artistPerformance: ArtistPerformanceCreateUpdateDto
        try {
            artistPerformance = service.createArtistPerformance(_artistPerformance)
            Log.d(TAG, "Created artistPerformance: $artistPerformance")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating artistPerformance", e)
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
            Log.d(TAG, "Updated artistPerformance: $artistPerformance")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating artistPerformance", e)
        }
        return artistPerformance
    }

    override suspend fun deleteArtistPerformance(id: Int) {
        try {
            service.deleteArtistPerformance(id)
            Log.d(TAG, "Deleted artistPerformance with: $id")
        } catch (e: Exception) {
            //Log.e(TAG, "Error deleting artistPerformance", e)
        }
    }
}