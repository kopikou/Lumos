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
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки зарплат", e)
        }
        return earnings
    }

    override suspend fun getEarningById(id: Int): Earning {
        lateinit var earning: Earning
        try {
            earning = service.getEarningById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки зарплаты", e)
        }
        return earning
    }

    override suspend fun createEarning(_earning: EarningCreateUpdateDto): EarningCreateUpdateDto {
        lateinit var earning: EarningCreateUpdateDto
        try {
            earning = service.createEarning(_earning)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания зарплаты", e)
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
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обноаления зарплаты", e)
        }
        return earning
    }

    override suspend fun deleteEarning(id: Int) {
        try {
            service.deleteEarning(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteEarningsByOrder(orderId: Int) {
        try {
            val allEarnings = getEarnings()
            val earningsToDelete = allEarnings.filter { it.order.id == orderId }

            earningsToDelete.forEach { earning ->
                deleteEarning(earning.id)
            }
        } catch (e: Exception) {
            null
        }
    }
}