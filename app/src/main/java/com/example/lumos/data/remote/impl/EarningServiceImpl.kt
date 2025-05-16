package com.example.lumos.data.remote.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.EarningCreateUpdateDto
import com.example.lumos.data.remote.api.EarningService
import com.example.lumos.data.remote.ApiClient

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

    override suspend fun createEarning(_earning: EarningCreateUpdateDto): EarningCreateUpdateDto {
        lateinit var earning: EarningCreateUpdateDto
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
        _earning: EarningCreateUpdateDto
    ): EarningCreateUpdateDto {
        lateinit var earning: EarningCreateUpdateDto
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

    suspend fun deleteEarningsByOrder(orderId: Int) {
        try {
            // First get all earnings for this order
            val allEarnings = getEarnings()
            val earningsToDelete = allEarnings.filter { it.order.id == orderId }

            // Delete each earning
            earningsToDelete.forEach { earning ->
                deleteEarning(earning.id)
            }
            Log.d(TAG, "Deleted all earnings for order: $orderId")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting earnings by order", e)
        }
    }
}