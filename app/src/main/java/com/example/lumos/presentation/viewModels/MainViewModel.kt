package com.example.lumos.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.usecases.GetArtistByNameUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    private val getArtistByNameUseCase: GetArtistByNameUseCase,
) : ViewModel() {
    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist> = _artist

    private val _authRequired = MutableLiveData<Boolean>()
    val authRequired: LiveData<Boolean> = _authRequired

    fun loadArtist(firstName: String, lastName: String) {
        viewModelScope.launch {
            try {
                val artist = getArtistByNameUseCase(firstName, lastName)
                _artist.postValue(artist)
            } catch (e: Exception) {
                Log.e("Artists", "Loading artists failed", e)
            }
        }
    }
}