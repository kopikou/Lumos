package com.example.lumos.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.domain.usecases.GetArtistDetailsUseCase
import com.example.lumos.domain.usecases.GetPerformanceArtistsUseCase
import com.example.lumos.domain.usecases.GetUnpaidArtistsUseCase
import com.example.lumos.domain.usecases.MarkEarningsAsPaidUseCase

class ManagementManagerViewModelFactory(
    private val getUnpaidArtists: GetUnpaidArtistsUseCase,
    private val markEarningsAsPaid: MarkEarningsAsPaidUseCase,
    private val getArtistDetails: GetArtistDetailsUseCase,
    private val getPerformanceArtists: GetPerformanceArtistsUseCase,
    private val artistRepository: ArtistRepositoryImpl,
    private val performanceRepository: PerformanceRepositoryImpl
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManagementManagerViewModel::class.java)) {
            return ManagementManagerViewModel(
                getUnpaidArtists,
                markEarningsAsPaid,
                getArtistDetails,
                getPerformanceArtists,
                artistRepository,
                performanceRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}