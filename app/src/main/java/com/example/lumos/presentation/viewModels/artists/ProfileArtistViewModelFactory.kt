package com.example.lumos.presentation.viewModels.artists

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.data.remote.impl.UserServiceImpl
import com.example.lumos.data.repository.ArtistRepositoryImpl
import com.example.lumos.data.repository.UserRepositoryImpl
import com.example.lumos.domain.usecases.GetArtistByNameUseCase
import com.example.lumos.domain.usecases.LogoutUseCase
import com.example.lumos.domain.usecases.UpdateArtistUseCase
import com.example.lumos.domain.usecases.UpdateUserUseCase

class ProfileArtistViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileArtistViewModel::class.java)) {
            return ProfileArtistViewModel(
                getArtistUseCase = createGetArtistUseCase(),
                updateArtistUseCase = createUpdateArtistUseCase(),
                updateUserUseCase = createUpdateUserUseCase(),
                logoutUseCase = createLogoutUseCase(),
                tokenManager = TokenManager(context),
                context = context
            ) as T
        }
        throw IllegalArgumentException("Неизвестная модель")
    }

    private fun createGetArtistUseCase(): GetArtistByNameUseCase {
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        return GetArtistByNameUseCase(artistRepository)
    }

    private fun createUpdateArtistUseCase(): UpdateArtistUseCase {
        val artistRepository = ArtistRepositoryImpl(ArtistServiceImpl())
        return UpdateArtistUseCase(artistRepository)
    }

    private fun createUpdateUserUseCase(): UpdateUserUseCase {
        val userRepository = UserRepositoryImpl(UserServiceImpl())
        return UpdateUserUseCase(userRepository)
    }

    private fun createLogoutUseCase(): LogoutUseCase {
        return LogoutUseCase(TokenManager(context))
    }
}