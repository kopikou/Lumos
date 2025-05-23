package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.domain.entities.Order
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GetOrdersUseCase(
    private val orderRepository: OrderRepositoryImpl
) {
    suspend operator fun invoke(onlyFutureOrders: Boolean = true): List<Order> {
        val allOrders = orderRepository.getOrders()
        return if (onlyFutureOrders) {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            allOrders.filter { order ->
                val orderDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .parse(order.date) ?: Date()
                !order.completed && !orderDate.before(today.time)
            }
        } else {
            allOrders
        }.sortedBy { it.date }
    }
}