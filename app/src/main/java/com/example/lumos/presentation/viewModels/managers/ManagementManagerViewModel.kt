package com.example.lumos.presentation.viewModels.managers

import android.util.Log
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
                Log.e("Payment", "Marking as paid failed", e)
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
                Log.e("Details", "Loading artists details failed", e)
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
                Log.e("Details", "Loading performances details failed", e)
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