package com.example.lumos.retrofit.services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.EarningCreateUpdateSerializer
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateSerializer
import com.example.lumos.domain.services.EarningService
import com.example.lumos.retrofit.ApiClient

class EarningServiceImpl: EarningService {
    val service = ApiClient.getEarningService()
    override suspend fun getEarnings(): List<Earning> {
        lateinit var earnings: List<Earning>
        try {
            earnings = service.getEarnings()
            Log.d(TAG, "Received earnings: $earnings")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching earnings", e)
        }
        return earnings
    }

    override suspend fun getEarningById(id: Int): Earning {
        lateinit var earning: Earning
        try {
            earning = service.getEarningById(id)
            Log.d(TAG, "Received earning: $earning")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching earning", e)
        }
        return earning
    }

    override suspend fun createEarning(_earning: EarningCreateUpdateSerializer): EarningCreateUpdateSerializer {
        lateinit var earning: EarningCreateUpdateSerializer
        try {
            earning = service.createEarning(_earning)
            Log.d(TAG, "Created earning: $earning")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating earning", e)
        }
        return earning
    }

    override suspend fun updateEarning(
        id: Int,
        _earning: EarningCreateUpdateSerializer
    ): EarningCreateUpdateSerializer {
        lateinit var earning: EarningCreateUpdateSerializer
        try {
            earning = service.updateEarning(id,_earning)
            Log.d(TAG, "Updated earning: $earning")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating earning", e)
        }
        return earning
    }

    override suspend fun deleteEarning(id: Int) {
        try {
            service.deleteEarning(id)
            Log.d(TAG, "Deleted earning with: $id")
        } catch (e: Exception) {
            //Log.e(TAG, "Error deleting earning", e)
        }
    }
}