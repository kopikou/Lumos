package com.example.lumos.retrofit.services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.lumos.domain.entities.Artist
import com.example.lumos.domain.services.ArtistService
import com.example.lumos.retrofit.ApiClient


class ArtistServiceImpl: ArtistService{
    val service = ApiClient.getArtistService()
    override suspend fun getArtists(): List<Artist> {
        lateinit var artists: List<Artist>
        try {
            artists = service.getArtists()
            Log.d(TAG, "Received artists: $artists")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching artists", e)
        }
        return artists
    }

    override suspend fun getArtistById(id: Int): Artist {
        lateinit var artist: Artist
        try {
            artist = service.getArtistById(id)
            Log.d(TAG, "Received artist: $artist")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching artist", e)
        }
        return artist
    }

    override suspend fun createArtist(_artist: Artist): Artist {
        lateinit var artist: Artist
        try {
            artist = service.createArtist(_artist)
            Log.d(TAG, "Created artist: $artist")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating artist", e)
        }
        return artist
    }

    override suspend fun updateArtist(id: Int, _artist: Artist): Artist {
        lateinit var artist: Artist
        try {
            artist = service.updateArtist(id,_artist)
            Log.d(TAG, "Updated artist: $artist")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating artist", e)
        }
        return artist
    }

    override suspend fun deleteArtist(id: Int): Unit {
        try {
            service.deleteArtist(id)
            Log.d(TAG, "Deleted artist with: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting artist", e)
        }
    }

}