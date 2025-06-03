package com.example.lumos.presentation.viewModels.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.domain.usecases.GetFinancialDataUseCase
import com.example.lumos.domain.usecases.LogoutUseCase

class ProfileManagerViewModelFactory(
    private val getFinancialDataUseCase: GetFinancialDataUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileManagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileManagerViewModel(
                getFinancialDataUseCase,
                logoutUseCase
            ) as T
        }
        throw IllegalArgumentException("Неизвестная модель")
    }
}