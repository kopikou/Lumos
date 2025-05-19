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

//class CreateOrderUseCase(
//    private val orderRepository: OrderRepositoryImpl,
//    private val earningRepository: EarningRepositoryImpl,
//    private val artistRepository: ArtistRepositoryImpl
//) {
//    suspend operator fun invoke(
//        orderDto: OrderCreateUpdateDto,
//        selectedArtists: List<Artist>,
//        performance: Performance,
//        artistPerformances: List<ArtistPerformance>
//    ) {
//        val createdOrder = orderRepository.createOrder(orderDto)
//
//        // Находим только что созданный заказ в списке
//        val orders = orderRepository.getOrders()
//        lateinit var addedOrder: Order
//        for (order in orders) {
//            if (order.date == createdOrder.date &&
//                order.location == createdOrder.location &&
//                order.amount == createdOrder.amount &&
//                order.comment == createdOrder.comment &&
//                order.performance.id == createdOrder.performance) {
//                addedOrder = order
//            }
//        }
//        //var artistPerformances: List<ArtistPerformance>
//
//        // Создаем записи о заработке для выбранных артистов
//        selectedArtists.forEach { artist ->
//            val artistPerformance = artistPerformances.firstOrNull {
//                it.artist.id == artist.id && it.performance.id == performance.id
//            }
//
//            if (artistPerformance != null) {
//                val earning = EarningCreateUpdateDto(
//                    order = addedOrder.id,
//                    artist = artist.id,
//                    amount = artistPerformance.rate.rate,
//                    paid = false
//                )
//                earningRepository.createEarning(earning)
//            }
//        }
//    }
//}

class CreateOrderUseCase(
    private val orderRepository: OrderRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl,
    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl
) {
    suspend operator fun invoke(
        date: String,
        location: String,
        performance: Performance,
        amount: Double,
        comment: String,
        selectedArtists: List<Artist>
    ): Result<Unit> {
        return try {
            // 1. Create order
            val orderDto = OrderCreateUpdateDto(
                date = date,
                location = location,
                performance = performance.id,
                amount = amount,
                comment = comment,
                completed = false
            )

            val createdOrder = orderRepository.createOrder(orderDto)

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

            // 2. Create earnings for artists
            selectedArtists.forEach { artist ->
                val artistPerformance = artistPerformanceRepository.getArtistPerformances()
                    .firstOrNull { it.artist.id == artist.id && it.performance.id == performance.id }

                artistPerformance?.let {
                    val earningDto = EarningCreateUpdateDto(
                        order = addedOrder.id,
                        artist = artist.id,
                        amount = it.rate.rate,
                        paid = false
                    )
                    earningRepository.createEarning(earningDto)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}