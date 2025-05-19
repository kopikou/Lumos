package com.example.lumos.domain.usecases

import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.domain.entities.Order
import com.example.lumos.domain.entities.OrderCreateUpdateDto

class UpdateOrderStatusUseCase(
    private val orderRepository: OrderRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(orderId: Int, isCompleted: Boolean): Boolean {
        try {
            // 1. Обновляем статус заказа
            val order = orderRepository.getOrderById(orderId)
            val updatedOrder = order.copy(completed = isCompleted)
            orderRepository.updateOrder(orderId, OrderCreateUpdateDto.fromOrder(updatedOrder))

            // 2. Если заказ выполнен, начисляем зарплату
            if (isCompleted) {
                calculateAndAddSalary(order)
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }

    private suspend fun calculateAndAddSalary(order: Order) {
        val artist = artistRepository.getArtistByName(
            tokenManager.getFirstName(),
            tokenManager.getLastName()
        )

        val earning = earningRepository.getEarnings()
            .firstOrNull { it.order.id == order.id && it.artist.id == artist.id }

        earning?.takeIf { !it.paid }?.let {
            val newBalance = artist.balance + it.amount
            artistRepository.updateArtist(artist.id, artist.copy(balance = newBalance))
        }
    }
}