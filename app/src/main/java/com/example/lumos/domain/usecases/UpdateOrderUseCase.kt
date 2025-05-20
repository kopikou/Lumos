package com.example.lumos.domain.usecases

import com.example.lumos.data.remote.impl.PerformanceServiceImpl
import com.example.lumos.data.repository.ArtistPerformanceRepositoryImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.EarningCreateUpdateDto
import com.example.lumos.domain.entities.OrderCreateUpdateDto
import com.example.lumos.domain.entities.Performance

//class UpdateOrderUseCase(
//    private val orderRepository: OrderRepositoryImpl,
//    private val earningRepository: EarningRepositoryImpl,
//    private val artistRepository: ArtistRepositoryImpl
//) {
//    suspend operator fun invoke(
//        orderId: Int,
//        orderDto: OrderCreateUpdateDto,
//        selectedArtists: List<Artist>,
//        isCompleted: Boolean
//    ) {
//        orderRepository.updateOrder(orderId, orderDto)
//
////        if (isCompleted) {
////            selectedArtists.forEach { artist ->
////                // Начисление зарплаты
////            }
////        }
//
//        // 2. Получаем текущие записи о заработке
//        val currentEarnings = earningRepository.getEarnings()
//            .filter { it.order.id == originalOrder.id }
//        println(currentEarnings)
//
//        // 3. Определяем, изменился ли номер или список артистов
//        val performanceChanged = originalOrder.performance.id != selectedPerformance.id
//        val artistsChanged = currentEarnings.map { it.artist.id }.sorted() !=
//                selectedArtists.map { it.id }.sorted()
//
//        // 4. Если номер или артисты изменились - удаляем старые записи и создаем новые
//        if (performanceChanged || artistsChanged) {
//            // Удаляем старые записи о заработке
//            currentEarnings.forEach { earning ->
//                earningService.deleteEarningsByOrder(originalOrder.id)
//            }
//
//            // Создаем новые записи о заработке для выбранных артистов
//            selectedArtists.forEach { artist ->
//                val artistPerformance = artistPerformances.firstOrNull {
//                    it.artist.id == artist.id && it.performance.id == selectedPerformance.id
//                }
//
//                artistPerformance?.let {
//                    val earning = EarningCreateUpdateDto(
//                        order = originalOrder.id,
//                        artist = artist.id,
//                        amount = it.rate.rate,
//                        paid = false
//                    )
//                    earningService.createEarning(earning)
//                }
//            }
//        }
//
//        // 5. Если заказ выполнен, начисляем зарплату
//        if (isCompleted) {
//            calculateAndAddSalaries(updatedOrder, selectedArtists)
//        }
//
//    }
//}
//class UpdateOrderUseCase(
//    private val orderRepository: OrderRepositoryImpl,
//    private val earningRepository: EarningRepositoryImpl,
//    private val artistPerformanceRepository: ArtistPerformanceRepositoryImpl,
//    private val artistRepository: ArtistRepositoryImpl
//) {
//    suspend operator fun invoke(
//        orderId: Int,
//        date: String,
//        location: String,
//        performance: Performance,
//        amount: Double,
//        comment: String,
//        isCompleted: Boolean,
//        selectedArtists: List<Artist>
//    ): Result<Unit> {
//        return try {
//            // 1. Update order
//            val orderDto = OrderCreateUpdateDto(
//                date = date,
//                location = location,
//                performance = performance.id,
//                amount = amount,
//                comment = comment,
//                completed = isCompleted
//            )
//
//            orderRepository.updateOrder(orderId, orderDto)
//
//            // 2. Handle earnings
//            val currentEarnings = earningRepository.getEarnings().filter { it.order.id == orderId }
//
//            // Check if performance or artists changed
//            val performanceChanged = currentEarnings.any {
//                it.order.performance.id != performance.id
//            }
//
//            val artistsChanged = currentEarnings.map { it.artist.id }.sorted() !=
//                    selectedArtists.map { it.id }.sorted()
//
//            if (performanceChanged || artistsChanged) {
//                // Remove old earnings
//                currentEarnings.forEach { earning ->
//                    earningRepository.deleteEarning(earning.id)
//                }
//
//                // Create new earnings
//                selectedArtists.forEach { artist ->
//                    val artistPerformance = artistPerformanceRepository.getArtistPerformances()
//                        .firstOrNull { it.artist.id == artist.id && it.performance.id == performance.id }
//
//                    artistPerformance?.let {
//                        val earningDto = EarningCreateUpdateDto(
//                            order = orderId,
//                            artist = artist.id,
//                            amount = it.rate.rate,
//                            paid = false
//                        )
//                        earningRepository.createEarning(earningDto)
//                    }
//                }
//            }
//
//            // 3. Handle completion
//            if (isCompleted) {
//                selectedArtists.forEach { artist ->
//                    val earning = earningRepository.getEarnings()
//                        .firstOrNull { it.order.id == orderId && it.artist.id == artist.id }
//
//                    earning?.takeIf { !it.paid }?.let {
//                        // Update artist balance
//                        val updatedArtist = artist.copy(balance = artist.balance + it.amount)
//                        artistRepository.updateArtist(artist.id, updatedArtist)
//
//                        // Mark earning as paid
//                        earningRepository.updateEarning(
//                            it.id,
//                            EarningCreateUpdateDto(
//                                order = orderId,
//                                artist = artist.id,
//                                amount = it.amount,
//                                paid = true
//                            )
//                        )
//                    }
//                }
//            }
//
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}

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
                performance = performance,//Performance(id = performanceId, title = "", cost = 0.0, cntArtists = 0),
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