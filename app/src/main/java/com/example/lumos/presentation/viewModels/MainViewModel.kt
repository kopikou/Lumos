package com.example.lumos.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Artist
import com.example.lumos.data.remote.impl.ArtistServiceImpl
import com.example.lumos.domain.usecases.CheckAuthStatusUseCase
import com.example.lumos.domain.usecases.GetArtistByNameUseCase
import kotlinx.coroutines.launch

//class MainViewModel : ViewModel() {
//    private val _artist = MutableLiveData<Artist>()
//    val artist: LiveData<Artist> = _artist
//
//    fun loadArtist(firstName: String, lastName: String) {
//        viewModelScope.launch {
//            try {
//                val artist = ArtistServiceImpl().getArtistByName(firstName, lastName)
//                _artist.postValue(artist)
//            } catch (e: Exception) {
//
//            }
//        }
//    }
//}
class MainViewModel(
    private val getArtistByNameUseCase: GetArtistByNameUseCase,
) : ViewModel() {
    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist> = _artist

    private val _authRequired = MutableLiveData<Boolean>()
    val authRequired: LiveData<Boolean> = _authRequired

//    fun checkAuth() {
//        _authRequired.value = !checkAuthStatusUseCase()
//    }

    fun loadArtist(firstName: String, lastName: String) {
        viewModelScope.launch {
            try {
                val artist = getArtistByNameUseCase(firstName, lastName)
                _artist.postValue(artist)
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }
}