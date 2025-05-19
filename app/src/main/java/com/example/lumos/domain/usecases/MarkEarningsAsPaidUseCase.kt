package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.domain.entities.EarningCreateUpdateDto

class MarkEarningsAsPaidUseCase(
    private val artistRepository: ArtistRepositoryImpl,
    private val earningRepository: EarningRepositoryImpl
) {
    suspend operator fun invoke(artistWithUnpaid: ArtistWithUnpaid) {
        // Обновляем баланс артиста
        val updatedArtist = artistWithUnpaid.artist.copy(
            balance = artistWithUnpaid.artist.balance - artistWithUnpaid.unpaidAmount
        )
        artistRepository.updateArtist(artistWithUnpaid.artist.id, updatedArtist)

        // Помечаем выплаты как оплаченные
        artistWithUnpaid.unpaidEarnings.forEach { earning ->
            val earningUpdate = EarningCreateUpdateDto(earning.order.id,earning.artist.id,earning.amount,true)
            earningRepository.updateEarning(earning.id, earningUpdate)
        }
    }
}