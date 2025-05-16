package com.example.lumos.data.remote

import android.content.Context
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.api.ArtistPerformanceService
import com.example.lumos.data.remote.api.ArtistService
import com.example.lumos.data.remote.api.AuthService
import com.example.lumos.data.remote.api.EarningService
import com.example.lumos.data.remote.api.OrderService
import com.example.lumos.data.remote.api.PerformanceService
import com.example.lumos.data.remote.api.ShowRateService
import com.example.lumos.data.remote.api.TypeService
import com.example.lumos.data.remote.api.UserService
import com.example.lumos.data.remote.auth.AuthInterceptor
import com.example.lumos.data.remote.auth.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"
    //private lateinit var tokenManager: TokenManager
    private var tokenManager: TokenManager? = null

    fun initialize(context: Context) {
        tokenManager = TokenManager(context)
    }
    // Общий клиент для всех запросов
    private val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            tokenManager?.let { manager ->
                addInterceptor(AuthInterceptor(manager))
                authenticator(TokenAuthenticator(manager))
            }
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.build()
    }

    // Единый экземпляр Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getArtistService(): ArtistService = retrofit.create(ArtistService::class.java)
    fun getTypeService(): TypeService = retrofit.create(TypeService::class.java)
    fun getShowRateService(): ShowRateService = retrofit.create(ShowRateService::class.java)
    fun getPerformanceService(): PerformanceService = retrofit.create(PerformanceService::class.java)
    fun getArtistPerformanceService(): ArtistPerformanceService = retrofit.create(
        ArtistPerformanceService::class.java)
    fun getOrderService(): OrderService = retrofit.create(OrderService::class.java)
    fun getEarningService(): EarningService = retrofit.create(EarningService::class.java)

    fun getAuthService(): AuthService = retrofit.create(AuthService::class.java)
    fun getUserService(): UserService = retrofit.create(UserService::class.java)
}