package com.example.lumos.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumos.domain.entities.Artist
import com.example.lumos.retrofit.services.ArtistServiceImpl
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist> = _artist

    fun loadArtist(firstName: String, lastName: String) {
        viewModelScope.launch {
            try {
                val artist = ArtistServiceImpl().getArtistByName(firstName, lastName)
                _artist.postValue(artist)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}