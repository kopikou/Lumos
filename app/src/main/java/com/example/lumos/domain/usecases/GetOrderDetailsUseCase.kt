package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.Order

class GetOrderDetailsUseCase(
    private val earningRepository: EarningRepositoryImpl
) {
    suspend operator fun invoke(orderId: Int, artistId: Int): OrderDetails {
        val earning = earningRepository.getEarnings()
            .firstOrNull { it.order.id == orderId && it.artist.id == artistId }

        return OrderDetails(
            order = earning?.order ?: throw Exception("Order not found"),
            earning = earning
        )
    }
}

data class OrderDetails(
    val order: Order,
    val earning: Earning?
)