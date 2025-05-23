package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.domain.entities.OrderCreateUpdateDto

class UpdateOrderStatusUseCase(
    private val orderRepository: OrderRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl
) {
    suspend operator fun invoke(orderId: Int, isCompleted: Boolean): Boolean {
        try {
            // 1. Обновляем статус заказа
            val order = orderRepository.getOrderById(orderId)
            val updatedOrder = order.copy(completed = isCompleted)
            orderRepository.updateOrder(orderId, OrderCreateUpdateDto.fromOrder(updatedOrder))

            // 2. Если заказ выполнен, начисляем зарплату
            if (isCompleted) {
                val earnings = earningRepository.getEarnings()
                    .filter{ it.order.id == orderId }
                earnings.forEach { earning ->
                    earning.takeIf { !it.paid }?.let {
                        val artist = artistRepository.getArtistById(earning.artist.id)
                        artistRepository.updateArtist(
                            earning.artist.id,
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