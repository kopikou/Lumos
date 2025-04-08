package com.example.lumos

import com.example.lumos.services.ArtistPerformanceService
import com.example.lumos.services.ArtistService
import com.example.lumos.services.EarningService
import com.example.lumos.services.OrderService
import com.example.lumos.services.PerformanceService
import com.example.lumos.services.ShowRateService
import com.example.lumos.services.TypeService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    fun getArtistService(): ArtistService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ArtistService::class.java)
    }
    fun getTypeService(): TypeService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TypeService::class.java)
    }
    fun getShowRateService(): ShowRateService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ShowRateService::class.java)
    }
    fun getPerformanceService(): PerformanceService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(PerformanceService::class.java)
    }
    fun getArtistPerformanceService(): ArtistPerformanceService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ArtistPerformanceService::class.java)
    }
    fun getOrderService(): OrderService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(OrderService::class.java)
    }
    fun getEarningService(): EarningService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(EarningService::class.java)
    }
}