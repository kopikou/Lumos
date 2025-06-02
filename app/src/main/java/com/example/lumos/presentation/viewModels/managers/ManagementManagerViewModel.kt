package com.example.lumos.presentation.viewModels.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.Performance
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.data.repository.ShowRateRepositoryImpl
import com.example.lumos.data.repository.TypeRepositoryImpl
import com.example.lumos.domain.entities.ArtistPerformanceCreateUpdateDto
import com.example.lumos.domain.entities.PerformanceCreateUpdateDto
import com.example.lumos.domain.entities.ShowRate
import com.example.lumos.domain.entities.Type
import com.example.lumos.domain.repositories.ShowRateRepository
import com.example.lumos.domain.usecases.AddPerformanceToArtistUseCase
import com.example.lumos.domain.usecases.ArtistDetails
import com.example.lumos.domain.usecases.ArtistWithUnpaid
import com.example.lumos.domain.usecases.CreateArtistUseCase
import com.example.lumos.domain.usecases.CreatePerformanceUseCase
import com.example.lumos.domain.usecases.DeleteArtistUseCase
import com.example.lumos.domain.usecases.DeletePerformanceUseCase
import com.example.lumos.domain.usecases.GetArtistDetailsUseCase
import com.example.lumos.domain.usecases.GetPerformanceArtistsUseCase
import com.example.lumos.domain.usecases.GetTypesUseCase
import com.example.lumos.domain.usecases.GetUnpaidArtistsUseCase
import com.example.lumos.domain.usecases.MarkEarningsAsPaidUseCase
import kotlinx.coroutines.launch

class ManagementManagerViewModel(
    private val getUnpaidArtists: GetUnpaidArtistsUseCase,
    private val markEarningsAsPaid: MarkEarningsAsPaidUseCase,
    private val getArtistDetails: GetArtistDetailsUseCase,
    private val getPerformanceArtists: GetPerformanceArtistsUseCase,
    private val artistRepository: ArtistRepositoryImpl,
    private val performanceRepository: PerformanceRepositoryImpl,
    private val typeRepository: TypeRepositoryImpl,
    private val createArtist: CreateArtistUseCase,
    private val deleteArtist: DeleteArtistUseCase,
    private val createPerformance: CreatePerformanceUseCase,
    private val deletePerformance: DeletePerformanceUseCase,
    private val getTypes: GetTypesUseCase,
    private val addPerformanceToArtist: AddPerformanceToArtistUseCase,
    private val showRateRepository: ShowRateRepositoryImpl
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
    val allTypes = MutableLiveData<List<Type>>()
    val ratesForType = MutableLiveData<List<ShowRate>>()

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
                Log.e("Выплаты", "Отметка выплаты не удалась", e)
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
                Log.e("Детали", "Загрузка артистов не удалась", e)
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
                Log.e("Детали", "Загрузка номеров не удалась", e)
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

    fun createNewArtist(firstName: String, lastName: String, phone: String) {
        viewModelScope.launch {
            try {
                val newArtist = Artist(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    balance = 0.0
                )
                createArtist(newArtist)
                loadArtistsCount()
            } catch (e: Exception) {
                Log.e("Артист", "Создание не удалось", e)
            }
        }
    }

    fun deleteArtistById(artistId: Int) {
        viewModelScope.launch {
            try {
                deleteArtist(artistId)
                loadArtistsCount()
            } catch (e: Exception) {
                Log.e("Артист", "Удаление не удалось", e)
            }
        }
    }

    fun createNewPerformance(
        title: String,
        duration: Int,
        cost: Double,
        typeId: Int,
        cntArtists: Int
    ) {
        viewModelScope.launch {
            try {
                val newPerformance = PerformanceCreateUpdateDto(
                    title = title,
                    duration = duration,
                    cost = cost,
                    type = typeId,
                    cntArtists = cntArtists
                )
                createPerformance(newPerformance)
                loadPerformancesCount()
            } catch (e: Exception) {
                Log.e("Номер", "Создание не удалось", e)
            }
        }
    }

    fun deletePerformanceById(performanceId: Int) {
        viewModelScope.launch {
            try {
                deletePerformance(performanceId)
                loadPerformancesCount()
            } catch (e: Exception) {
                Log.e("Номер", "Создание не удалось", e)
            }
        }
    }

    fun loadTypes() {
        viewModelScope.launch {
            try {
                allTypes.value = getTypes()
            } catch (e: Exception) {
                Log.e("Types", "Loading failed", e)
            }
        }
    }

    fun addPerformanceToArtist(artistId: Int, performanceId: Int, rateId: Int) {
        viewModelScope.launch {
            try {
                val dto = ArtistPerformanceCreateUpdateDto(
                    artist = artistId,
                    performance = performanceId,
                    rate = rateId
                )
                addPerformanceToArtist(dto)
                // Обновляем детали артиста после добавления номера
                loadArtistDetails(artistId)
            } catch (e: Exception) {
                Log.e("ArtistPerformance", "Failed to add performance to artist", e)
            }
        }
    }

    fun loadRatesForType(typeId: Int) {
        viewModelScope.launch {
            try {
                val allRates = showRateRepository.getShowRates()
                ratesForType.value = allRates.filter { it.showType.id == typeId }
            } catch (e: Exception) {
                ratesForType.value = emptyList()
                Log.e("Rates", "Failed to load rates", e)
            }
        }
    }
}