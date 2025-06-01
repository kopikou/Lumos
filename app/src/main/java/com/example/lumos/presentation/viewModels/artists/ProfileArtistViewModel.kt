package com.example.lumos.presentation.viewModels.artists
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.R
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
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModel() {

    private val _artist = MutableLiveData<Artist?>()
    val artist: LiveData<Artist?> = _artist

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun getSavedFirstName(): String? = tokenManager.getFirstName()
    fun getSavedLastName(): String? = tokenManager.getLastName()
    fun getUserId(): Int? = tokenManager.getUserId()

    fun loadArtist(firstName: String, lastName: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _artist.value = getArtistUseCase(firstName, lastName)
                _error.value = null
            } catch (e: Exception) {
                _error.value = context.getString(R.string.artist_load_error)
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
            val updatedArtist = _artist.value?.copy(
                firstName = firstName,
                lastName = lastName,
                phone = phone
            ) ?: return false

            updateArtistUseCase(artistId, updatedArtist)

            updateUserUseCase(userId, UserUpdateRequest(firstName, lastName))

            _artist.value = updatedArtist
            tokenManager.saveUserNames(firstName, lastName)
            true
        } catch (e: Exception) {
            _error.value = context.getString(R.string.profile_update_error)
            false
        }
    }

    fun logout() {
        logoutUseCase()
    }
}