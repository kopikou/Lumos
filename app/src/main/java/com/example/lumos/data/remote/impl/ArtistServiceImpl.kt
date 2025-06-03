package com.example.lumos.data.remote.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Artist
import com.example.lumos.data.remote.api.ArtistService
import com.example.lumos.data.remote.ApiClient


class ArtistServiceImpl: ArtistService {
    val service = ApiClient.getArtistService()
    override suspend fun getArtists(): List<Artist> {
        lateinit var artists: List<Artist>
        try {
            artists = service.getArtists()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки артистов", e)
        }
        return artists
    }

    override suspend fun getArtistById(id: Int): Artist {
        lateinit var artist: Artist
        try {
            artist = service.getArtistById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки артиста", e)
        }
        return artist
    }

    override suspend fun createArtist(_artist: Artist): Artist {
        lateinit var artist: Artist
        try {
            artist = service.createArtist(_artist)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания артиста", e)
        }
        return artist
    }

    override suspend fun updateArtist(id: Int, _artist: Artist): Artist {
        lateinit var artist: Artist
        try {
            artist = service.updateArtist(id,_artist)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обновления артиста", e)
        }
        return artist
    }

    override suspend fun deleteArtist(id: Int){
        try {
            service.deleteArtist(id)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getArtistByName(firstName: String, lastName: String): Artist {
        lateinit var artist: Artist
        try {
            val artists = getArtists()
            for (artist in artists) {
                if (artist.firstName == firstName && artist.lastName == lastName) {
                    return artist
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки артиста", e)
        }
        return artist
    }
}