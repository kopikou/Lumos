package com.example.lumos

import android.app.Application
import com.example.lumos.retrofit.ApiClient

class Lumos : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)  // Инициализируем ApiClient при старте приложения

    }
}