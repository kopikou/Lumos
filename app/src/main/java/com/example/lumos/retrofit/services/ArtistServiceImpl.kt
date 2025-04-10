package com.example.lumos.retrofit.services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.services.ArtistService
import com.example.lumos.retrofit.ApiClient

class ArtistServiceImpl: ArtistService{
    override suspend fun getArtists(): List<Artist> {
        lateinit var artists: List<Artist>
        try {
            artists = ApiClient.getArtistService().getArtists()
            Log.d(TAG, "Received artists: $artists")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching artists", e)
        }
        return artists
    }

    override suspend fun getArtistById(id: Int): Artist {
        lateinit var artist: Artist
        try {
            artist = ApiClient.getArtistService().getArtistById(id)
            Log.d(TAG, "Received artist: $artist")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching artist", e)
        }
        return artist
    }

    override suspend fun createArtist(_artist: Artist): Artist {
        lateinit var artist: Artist
        try {
            artist = ApiClient.getArtistService().createArtist(_artist)
            Log.d(TAG, "Created artist: $artist")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating artist", e)
        }
        return artist
    }

    override suspend fun updateArtist(id: Int, artist: Artist): Artist {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArtist(id: Int) {
        TODO("Not yet implemented")
    }

}