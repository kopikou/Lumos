package com.example.lumos.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.domain.usecases.GetArtistByNameUseCase

class MainViewModelFactory(
    private val getArtistUseCase: GetArtistByNameUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(getArtistUseCase) as T
        }
        throw IllegalArgumentException("Неизвестная модель")
    }
}