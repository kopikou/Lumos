package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.Earning

class GetUnpaidArtistsUseCase(
    private val earningRepository: EarningRepositoryImpl,
    private val orderRepository: OrderRepositoryImpl
) {
    suspend operator fun invoke(): List<ArtistWithUnpaid> {
        val earnings = earningRepository.getEarnings()
        val orders = orderRepository.getOrders()

        val unpaidEarnings = earnings.filter { earning ->
            !earning.paid && orders.any { order ->
                order.id == earning.order.id && order.completed
            }
        }

        return unpaidEarnings.groupBy { it.artist }
            .map { (artist, earnings) ->
                ArtistWithUnpaid(
                    artist = artist,
                    unpaidAmount = earnings.sumOf { it.amount },
                    unpaidEarnings = earnings
                )
            }
    }
}

data class ArtistWithUnpaid(
    val artist: Artist,
    val unpaidAmount: Double,
    val unpaidEarnings: List<Earning>
)