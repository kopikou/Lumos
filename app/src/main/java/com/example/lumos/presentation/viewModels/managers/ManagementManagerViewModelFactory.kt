package com.example.lumos.presentation.viewModels.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.PerformanceRepositoryImpl
import com.example.lumos.data.repository.ShowRateRepositoryImpl
import com.example.lumos.data.repository.TypeRepositoryImpl
import com.example.lumos.domain.usecases.AddPerformanceToArtistUseCase
import com.example.lumos.domain.usecases.CreateArtistUseCase
import com.example.lumos.domain.usecases.CreatePerformanceUseCase
import com.example.lumos.domain.usecases.DeleteArtistUseCase
import com.example.lumos.domain.usecases.DeletePerformanceUseCase
import com.example.lumos.domain.usecases.GetArtistDetailsUseCase
import com.example.lumos.domain.usecases.GetPerformanceArtistsUseCase
import com.example.lumos.domain.usecases.GetTypesUseCase
import com.example.lumos.domain.usecases.GetUnpaidArtistsUseCase
import com.example.lumos.domain.usecases.MarkEarningsAsPaidUseCase

class ManagementManagerViewModelFactory(
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
                performanceRepository,
                typeRepository,
                createArtist,
                deleteArtist,
                createPerformance,
                deletePerformance,
                getTypes,
                addPerformanceToArtist,
                showRateRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}