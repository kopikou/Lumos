package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.domain.entities.Artist
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GetArtistsForOrderUseCase (
    private val earningRepository: EarningRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl){
    suspend operator fun invoke(orderId: Int): List<Artist> {
        // 1. Получаем все записи о заработке для данного заказа
        val earnings = earningRepository.getEarnings()
            .filter { it.order.id == orderId }

        // 2. Получаем ID всех артистов из этих записей
        val artistIds = earnings.map { it.artist.id }.distinct()

        // 3. Получаем полные данные об артистах
        return artistIds.mapNotNull { artistId ->
            try {
                artistRepository.getArtistById(artistId)
            } catch (e: Exception) {
                null // Игнорируем артистов, которые не найдены
            }
        }
    }
}