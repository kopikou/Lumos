package com.example.lumos.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.Performance
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.usecases.ArtistDetails
import com.example.lumos.domain.usecases.ArtistWithUnpaid
import com.example.lumos.domain.usecases.GetArtistDetailsUseCase
import com.example.lumos.domain.usecases.GetPerformanceArtistsUseCase
import com.example.lumos.domain.usecases.GetUnpaidArtistsUseCase
import com.example.lumos.domain.usecases.MarkEarningsAsPaidUseCase
import kotlinx.coroutines.launch

//class ManagementViewModel : ViewModel() {
//    private val earningService = EarningServiceImpl()
//    private val artistService = ArtistServiceImpl()
//    private val orderService = OrderServiceImpl()
//
//    val unpaidEarningsCount = MutableLiveData<Int>()
//    val unpaidArtists = MutableLiveData<List<ArtistWithUnpaid>>()
//
//    private val artistPerformanceService = ArtistPerformanceServiceImpl()
//    private val performanceService = PerformanceServiceImpl()
//
//    val artistsCount = MutableLiveData<Int>()
//    val allArtists = MutableLiveData<List<Artist>>()
//    val artistDetails = MutableLiveData<ArtistDetails>()
//
//    val performancesCount = MutableLiveData<Int>()
//    val allPerformances = MutableLiveData<List<Performance>>()
//    val performanceDetails = MutableLiveData<Performance>()
//    val performanceArtists = MutableLiveData<List<Artist>>()
//
//    data class ArtistWithUnpaid(
//        val artist: Artist,
//        val unpaidAmount: Double,
//        val unpaidEarnings: List<Earning> // Добавляем список невыплаченных earnings
//    )
//
//    data class ArtistDetails(
//        val artist: Artist,
//        val performances: List<PerformanceWithRate>
//    )
//
//    data class PerformanceWithRate(
//        val performance: Performance,
//        val rate: Double
//    )
//
//    fun loadUnpaidEarnings() {
//        viewModelScope.launch {
//            try {
//                val earnings = earningService.getEarnings()
//                val orders = orderService.getOrders()
//
//                // Фильтруем только невыплаченные earnings по завершенным заказам
//                val unpaidEarnings = earnings.filter { earning ->
//                    !earning.paid && orders.any { order ->
//                        order.id == earning.order.id && order.completed
//                    }
//                }
//
//                unpaidEarningsCount.postValue(unpaidEarnings.size)
//
//                // Группируем по артистам и суммируем невыплаченные суммы
//                val artistsMap = unpaidEarnings.groupBy { it.artist }
//                    .map { (artist, earnings) ->
//                        ArtistWithUnpaid(
//                            artist = artist,
//                            unpaidAmount = earnings.sumOf { it.amount },
//                            unpaidEarnings = earnings
//                        )
//                    }
//
//                unpaidArtists.postValue(artistsMap)
//            } catch (e: Exception) {
//                Log.e("ManagementVM", "Error loading unpaid earnings", e)
//                unpaidEarningsCount.postValue(0)
//                unpaidArtists.postValue(emptyList())
//            }
//        }
//    }
//
//    fun markAsPaid(artistWithUnpaid: ArtistWithUnpaid) {
//        viewModelScope.launch {
//            try {
//                // Обновляем баланс артиста (вычитаем только сумму по завершенным заказам)
//                val updatedArtist = artistWithUnpaid.artist.copy(
//                    balance = artistWithUnpaid.artist.balance - artistWithUnpaid.unpaidAmount
//                )
//                artistService.updateArtist(artistWithUnpaid.artist.id, updatedArtist)
//
//                // Помечаем как paid только связанные earnings
//                artistWithUnpaid.unpaidEarnings.forEach { earning ->
//                    val updatedEarning = EarningCreateUpdateDto.fromEarning(
//                        earning.copy(paid = true)
//                    )
//                    earningService.updateEarning(earning.id, updatedEarning)
//                }
//
//                // Обновляем данные
//                loadUnpaidEarnings()
//            } catch (e: Exception) {
//                Log.e("ManagementVM", "Error marking as paid", e)
//            }
//        }
//    }
//
//    fun loadArtistsCount() {
//        viewModelScope.launch {
//            try {
//                val artists = artistService.getArtists()
//                artistsCount.postValue(artists.size)
//                allArtists.postValue(artists)
//            } catch (e: Exception) {
//                Log.e("ManagementVM", "Error loading artists count", e)
//                artistsCount.postValue(0)
//            }
//        }
//    }
//
//    fun loadArtistDetails(artistId: Int) {
//        viewModelScope.launch {
//            try {
//                val artist = artistService.getArtistById(artistId)
//                val artistPerformances = artistPerformanceService.getArtistPerformances()
//                    .filter { it.artist.id == artistId }
//
//                val performancesWithRates = artistPerformances.map { ap ->
//                    val performance = performanceService.getPerformanceById(ap.performance.id)
//                    PerformanceWithRate(performance, ap.rate.rate)
//                }
//
//                artistDetails.postValue(ArtistDetails(artist, performancesWithRates))
//            } catch (e: Exception) {
//                Log.e("ManagementVM", "Error loading artist details", e)
//            }
//        }
//    }
//
//    fun loadPerformancesCount() {
//        viewModelScope.launch {
//            try {
//                val performances = performanceService.getPerformances()
//                performancesCount.postValue(performances.size)
//                allPerformances.postValue(performances)
//            } catch (e: Exception) {
//                Log.e("ManagementVM", "Error loading performances count", e)
//                performancesCount.postValue(0)
//            }
//        }
//    }
//
//    fun loadPerformanceDetails(performanceId: Int) {
//        viewModelScope.launch {
//            try {
//                val performance = performanceService.getPerformanceById(performanceId)
//                performanceDetails.postValue(performance)
//            } catch (e: Exception) {
//                Log.e("ManagementVM", "Error loading performance details", e)
//            }
//        }
//    }
//
//    fun loadArtistsForPerformance(performance: Performance) {
//        viewModelScope.launch {
//            try {
//                // Получаем все связи артист-номер
//                val artistPerformances = artistPerformanceService.getArtistPerformances()
//                    .filter { it.performance == performance }
//
//                // Получаем данные всех артистов
//                val allArtists = artistService.getArtists()
//
//                // Фильтруем только тех артистов, которые работают в этом номере
//                val artists = artistPerformances.map { ap ->
//                    allArtists.find { it == ap.artist }!!
//                }
//
//                performanceArtists.postValue(artists)
//            } catch (e: Exception) {
//                Log.e("ManagementVM", "Error loading performance artists", e)
//                performanceArtists.postValue(emptyList())
//            }
//        }
//    }
//}
class ManagementManagerViewModel(
    private val getUnpaidArtists: GetUnpaidArtistsUseCase,
    private val markEarningsAsPaid: MarkEarningsAsPaidUseCase,
    private val getArtistDetails: GetArtistDetailsUseCase,
    private val getPerformanceArtists: GetPerformanceArtistsUseCase,
    private val artistRepository: ArtistRepositoryImpl,
    private val performanceRepository: PerformanceRepositoryImpl
) : ViewModel() {
    val unpaidEarningsCount = MutableLiveData<Int>()
    val unpaidArtists = MutableLiveData<List<ArtistWithUnpaid>>()
    val artistsCount = MutableLiveData<Int>()
    val allArtists = MutableLiveData<List<Artist>>()
    val artistDetails = MutableLiveData<ArtistDetails>()
    val performancesCount = MutableLiveData<Int>()
    val allPerformances = MutableLiveData<List<Performance>>()
    val performanceDetails = MutableLiveData<Performance>()
    val performanceArtists = MutableLiveData<List<Artist>>()

    fun loadUnpaidEarnings() {
        viewModelScope.launch {
            try {
                val result = getUnpaidArtists()
                unpaidEarningsCount.value = result.size
                unpaidArtists.value = result
            } catch (e: Exception) {
                unpaidEarningsCount.value = 0
                unpaidArtists.value = emptyList()
            }
        }
    }

    fun markAsPaid(artistWithUnpaid: ArtistWithUnpaid) {
        viewModelScope.launch {
            try {
                markEarningsAsPaid(artistWithUnpaid)
                loadUnpaidEarnings()
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }

    fun loadArtistsCount() {
        viewModelScope.launch {
            try {
                val artists = artistRepository.getArtists()
                artistsCount.value = artists.size
                allArtists.value = artists
            } catch (e: Exception) {
                artistsCount.value = 0
            }
        }
    }

    fun loadArtistDetails(artistId: Int) {
        viewModelScope.launch {
            try {
                artistDetails.value = getArtistDetails(artistId)!!
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }

    fun loadPerformancesCount() {
        viewModelScope.launch {
            try {
                val performances = performanceRepository.getPerformances()
                performancesCount.value = performances.size
                allPerformances.value = performances
            } catch (e: Exception) {
                performancesCount.value = 0
            }
        }
    }

    fun loadPerformanceDetails(performanceId: Int) {
        viewModelScope.launch {
            try {
                performanceDetails.value = performanceRepository.getPerformanceById(performanceId)
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }

    fun loadArtistsForPerformance(performanceId: Int) {
        viewModelScope.launch {
            try {
                performanceArtists.value = getPerformanceArtists(performanceId)!!
            } catch (e: Exception) {
                performanceArtists.value = emptyList()
            }
        }
    }
}