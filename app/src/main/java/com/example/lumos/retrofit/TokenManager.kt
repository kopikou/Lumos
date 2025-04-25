package com.example.lumos.retrofit

import android.content.Context
//Хранение токенов
//class TokenManager(context: Context) {
//    private val sharedPreferences = context.getSharedPreferences("tokens", Context.MODE_PRIVATE)
//
//    fun saveTokens(accessToken: String, refreshToken: String) {
//        sharedPreferences.edit()
//            .putString("access", accessToken)
//            .putString("refresh", refreshToken)
//            .apply()
//    }
//
//    fun getAccessToken(): String? = sharedPreferences.getString("access", null)
//    fun getRefreshToken(): String? = sharedPreferences.getString("refresh", null)
//
//    fun clearTokens() {
//        sharedPreferences.edit().clear().apply()
//    }
//}
class TokenManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("tokens", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString("access", accessToken)
            .putString("refresh", refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access", null)
    fun getRefreshToken(): String? = sharedPreferences.getString("refresh", null)

    fun hasValidToken(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }
}