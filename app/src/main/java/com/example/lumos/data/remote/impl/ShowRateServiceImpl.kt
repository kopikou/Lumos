package com.example.lumos.data.remote.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.ShowRateCreateUpdateDto
import com.example.lumos.domain.entities.ShowRate
import com.example.lumos.data.remote.api.ShowRateService
import com.example.lumos.data.remote.ApiClient

class ShowRateServiceImpl: ShowRateService {
    val service = ApiClient.getShowRateService()
    override suspend fun getShowRates(): List<ShowRate> {
        lateinit var showRates: List<ShowRate>
        try {
            showRates = service.getShowRates()
            Log.d(TAG, "Received showRates: $showRates")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching showRates", e)
        }
        return showRates
    }

    override suspend fun getShowRateById(id: Int): ShowRate {
        lateinit var showRate: ShowRate
        try {
            showRate = service.getShowRateById(id)
            Log.d(TAG, "Received showRate: $showRate")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching showRate", e)
        }
        return showRate
    }

    override suspend fun createShowRate(_showRate: ShowRateCreateUpdateDto): ShowRateCreateUpdateDto {
    lateinit var showRate: ShowRateCreateUpdateDto
    try {
        showRate = service.createShowRate(_showRate)
        Log.d(TAG, "Created showRate: $showRate")
    } catch (e: Exception) {
        Log.e(TAG, "Error creating showRate", e)
    }
    return showRate
}

    override suspend fun updateShowRate(id: Int, _showRate: ShowRateCreateUpdateDto): ShowRateCreateUpdateDto {
        lateinit var showRate: ShowRateCreateUpdateDto
        try {
            showRate = service.updateShowRate(id,_showRate)
            Log.d(TAG, "Updated showRate: $showRate")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating showRate", e)
        }
        return showRate
    }

    override suspend fun deleteShowRate(id: Int) {
        try {
            service.deleteShowRate(id)
            Log.d(TAG, "Deleted showRate with: $id")
        } catch (e: Exception) {
            //Log.e(TAG, "Error deleting showRate", e)
        }
    }
}