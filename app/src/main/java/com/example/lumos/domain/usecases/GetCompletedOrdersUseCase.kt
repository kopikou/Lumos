package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.Order
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GetCompletedOrdersUseCase(
    private val earningRepository: EarningRepositoryImpl,
    private val artistId: Int
) {
    suspend operator fun invoke(artistId: Int): Pair<List<Order>, Map<Int, Earning>> {
        val earnings = earningRepository.getEarnings()
            .filter { it.artist.id == artistId }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val completedOrders = earnings
            .map { it.order }
            .filter { order ->
                val orderDate = parseDate(order.date)
                order.completed && !orderDate.after(today.time)
            }
            .sortedByDescending { it.date }

        val earningsMap = earnings.associateBy { it.order.id }

        return completedOrders to earningsMap
    }

    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (e: ParseException) {
            Date()
        }
    }
}