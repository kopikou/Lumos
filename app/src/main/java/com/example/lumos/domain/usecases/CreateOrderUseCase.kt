package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.ArtistPerformance
import com.example.lumos.domain.entities.EarningCreateUpdateDto
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateDto
import com.example.lumos.domain.entities.Performance

class CreateOrderUseCase(
    private val orderRepository: OrderRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl,
    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl
) {
    suspend operator fun invoke(
        date: String,
        performanceId: Int,
        location: String,
        amount: Double,
        comment: String,
        artistIds: List<Int>
    ): Boolean {
        try {
            // 1. Создаем заказ
            val newOrder = OrderCreateUpdateDto(
                date = date,
                location = location,
                performance = performanceId,
                amount = amount,
                comment = comment,
                completed = false
            )
            val createdOrder = orderRepository.createOrder(newOrder)

            val orders = orderRepository.getOrders()
            lateinit var addedOrder: Order
            for (order in orders) {
                if (order.date == createdOrder.date &&
                    order.location == createdOrder.location &&
                    order.amount == createdOrder.amount &&
                    order.comment == createdOrder.comment &&
                    order.performance.id == createdOrder.performance) {
                    addedOrder = order
                }
            }

            // 2. Создаем записи о заработке для артистов
            artistIds.forEach { artistId ->
                val artistPerformance = artistPerformanceRepository.getArtistPerformances()
                    .firstOrNull { it.artist.id == artistId && it.performance.id == performanceId }

                artistPerformance?.let {
                    earningRepository.createEarning(
                        EarningCreateUpdateDto(
                            order = addedOrder.id,
                            artist = artistId,
                            amount = it.rate.rate,
                            paid = false
                        )
                    )
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}