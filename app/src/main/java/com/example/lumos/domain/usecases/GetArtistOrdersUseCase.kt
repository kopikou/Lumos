package com.example.lumos.domain.usecases

import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.domain.entities.Order
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GetArtistOrdersUseCase(
    private val artistRepository: ArtistRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): List<Order> {
        val artist = artistRepository.getArtistByName(
            tokenManager.getFirstName(),
            tokenManager.getLastName()
        )

        val earnings = earningRepository.getEarnings()
            .filter { it.artist.id == artist.id }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return earnings
            .map { it.order }
            .filter { order ->
                val orderDate = parseDate(order.date)
                !order.completed && !orderDate.before(today.time)
            }
            .sortedBy { it.date }
    }

    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (e: ParseException) {
            Date()
        }
    }
}