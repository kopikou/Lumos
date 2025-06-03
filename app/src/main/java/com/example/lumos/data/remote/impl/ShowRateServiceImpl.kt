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
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки ставок", e)
        }
        return showRates
    }

    override suspend fun getShowRateById(id: Int): ShowRate {
        lateinit var showRate: ShowRate
        try {
            showRate = service.getShowRateById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки ставки", e)
        }
        return showRate
    }

    override suspend fun createShowRate(_showRate: ShowRateCreateUpdateDto): ShowRateCreateUpdateDto {
    lateinit var showRate: ShowRateCreateUpdateDto
    try {
        showRate = service.createShowRate(_showRate)
    } catch (e: Exception) {
        Log.e(TAG, "Ошибка создания ставки", e)
    }
    return showRate
}

    override suspend fun updateShowRate(id: Int, _showRate: ShowRateCreateUpdateDto): ShowRateCreateUpdateDto {
        lateinit var showRate: ShowRateCreateUpdateDto
        try {
            showRate = service.updateShowRate(id,_showRate)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обновления ставки", e)
        }
        return showRate
    }

    override suspend fun deleteShowRate(id: Int) {
        try {
            service.deleteShowRate(id)
        } catch (e: Exception) {
            null
        }
    }
}