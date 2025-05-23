package com.example.lumos.presentation.viewModels.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.domain.usecases.GetArtistByNameUseCase
import com.example.lumos.domain.usecases.LogoutUseCase
import com.example.lumos.domain.usecases.UpdateArtistUseCase
import com.example.lumos.domain.usecases.UpdateUserUseCase

class ProfileArtistViewModelFactory(
    private val getArtistUseCase: GetArtistByNameUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileArtistViewModel::class.java)) {
            return ProfileArtistViewModel(
                getArtistUseCase,
                updateArtistUseCase,
                updateUserUseCase,
                logoutUseCase,
                tokenManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}