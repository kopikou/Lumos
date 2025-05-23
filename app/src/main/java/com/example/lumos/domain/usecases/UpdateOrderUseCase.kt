package com.example.lumos.domain.usecases

import com.example.lumos.data.remote.impl.PerformanceServiceImpl
import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.entities.EarningCreateUpdateDto
import com.example.lumos.domain.entities.OrderCreateUpdateDto

class UpdateOrderUseCase(
    private val orderRepository: OrderRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl,
    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl
) {
    suspend operator fun invoke(
        orderId: Int,
        date: String,
        performanceId: Int,
        location: String,
        amount: Double,
        comment: String,
        isCompleted: Boolean,
        artistIds: List<Int>
    ): Boolean {
        val performanceRepositoryImpl = PerformanceRepositoryImpl(PerformanceServiceImpl())
        val performance = performanceRepositoryImpl.getPerformanceById(performanceId)
        try {
            // 1. Обновляем заказ
            val order = orderRepository.getOrderById(orderId)
            val updatedOrder = order.copy(
                date = date,
                location = location,
                performance = performance,
                amount = amount,
                comment = comment,
                completed = isCompleted
            )
            orderRepository.updateOrder(orderId, OrderCreateUpdateDto.fromOrder(updatedOrder))

            // 2. Если изменился номер или артисты, обновляем записи о заработке
            val currentEarnings = earningRepository.getEarnings().filter { it.order.id == orderId }
            val performanceChanged = order.performance.id != performanceId
            val artistsChanged = currentEarnings.map { it.artist.id } != artistIds

            if (performanceChanged || artistsChanged) {
                // Удаляем старые записи
                currentEarnings.forEach { earning ->
                    earningRepository.deleteEarning(earning.id)
                }

                // Создаем новые
                artistIds.forEach { artistId ->
                    val artistPerformance = artistPerformanceRepository.getArtistPerformances()
                        .firstOrNull { it.artist.id == artistId && it.performance.id == performanceId }

                    artistPerformance?.let {
                        earningRepository.createEarning(
                            EarningCreateUpdateDto(
                                order = orderId,
                                artist = artistId,
                                amount = it.rate.rate,
                                paid = false
                            )
                        )
                    }
                }
            }

            // 3. Если заказ выполнен, начисляем зарплату
            if (isCompleted) {
                artistIds.forEach { artistId ->
                    val earning = earningRepository.getEarnings()
                        .firstOrNull { it.order.id == orderId && it.artist.id == artistId }

                    earning?.takeIf { !it.paid }?.let {
                        val artist = artistRepository.getArtistById(artistId)
                        artistRepository.updateArtist(
                            artistId,
                            artist.copy(balance = artist.balance + it.amount)
                        )
                    }
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}