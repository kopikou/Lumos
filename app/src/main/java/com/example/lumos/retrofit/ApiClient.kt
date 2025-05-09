package com.example.lumos.retrofit

import android.content.Context
import com.example.lumos.domain.services.ArtistPerformanceService
import com.example.lumos.domain.services.ArtistService
import com.example.lumos.domain.services.AuthService
import com.example.lumos.domain.services.EarningService
import com.example.lumos.domain.services.OrderService
import com.example.lumos.domain.services.PerformanceService
import com.example.lumos.domain.services.ShowRateService
import com.example.lumos.domain.services.TypeService
import com.example.lumos.domain.services.UserService
import com.example.lumos.retrofit.authentification.AuthInterceptor
import com.example.lumos.retrofit.authentification.TokenAuthenticator
import com.example.lumos.retrofit.authentification.TokenManager
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
//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(AuthInterceptor(tokenManager))
//        .authenticator(TokenAuthenticator(tokenManager))
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        })
//        .build()
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
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(okHttpClient) // используем общий клиент
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
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
    fun getArtistPerformanceService(): ArtistPerformanceService = retrofit.create(ArtistPerformanceService::class.java)
    fun getOrderService(): OrderService = retrofit.create(OrderService::class.java)
    fun getEarningService(): EarningService = retrofit.create(EarningService::class.java)

    fun getAuthService(): AuthService = retrofit.create(AuthService::class.java)
    fun getUserService(): UserService = retrofit.create(UserService::class.java)
}