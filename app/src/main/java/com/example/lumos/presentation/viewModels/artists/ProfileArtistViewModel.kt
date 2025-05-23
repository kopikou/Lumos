package com.example.lumos.presentation.viewModels.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.entities.UserUpdateRequest
import com.example.lumos.domain.usecases.GetArtistByNameUseCase
import com.example.lumos.domain.usecases.LogoutUseCase
import com.example.lumos.domain.usecases.UpdateArtistUseCase
import com.example.lumos.domain.usecases.UpdateUserUseCase
import kotlinx.coroutines.launch

class ProfileArtistViewModel(
    private val getArtistUseCase: GetArtistByNameUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _artist = MutableLiveData<Artist?>()
    val artist: LiveData<Artist?> = _artist

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadArtist(firstName: String, lastName: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _artist.value = getArtistUseCase(firstName, lastName)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных артиста"
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun updateProfile(
        artistId: Int,
        userId: Int,
        firstName: String,
        lastName: String,
        phone: String
    ): Boolean {
        return try {
            // Обновляем артиста
            val updatedArtist = _artist.value?.copy(
                firstName = firstName,
                lastName = lastName,
                phone = phone
            ) ?: return false

            updateArtistUseCase(artistId, updatedArtist)

            // Обновляем пользователя
            updateUserUseCase(userId, UserUpdateRequest(firstName, lastName))

            // Обновляем локальные данные
            _artist.value = updatedArtist
            tokenManager.saveUserNames(firstName, lastName)
            true
        } catch (e: Exception) {
            _error.value = "Ошибка обновления профиля"
            false
        }
    }
    fun logout() {
        logoutUseCase()
    }
}