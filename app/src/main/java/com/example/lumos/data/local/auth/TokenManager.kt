package com.example.lumos.data.local.auth

import android.content.Context
//Хранение токенов
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

    fun saveAdminStatus(isAdmin: Boolean) {
        sharedPreferences.edit().putBoolean("is_admin", isAdmin).apply()
    }

    fun isAdmin(): Boolean {
        return sharedPreferences.getBoolean("is_admin", false)
    }

    fun saveUserNames(firstName: String?, lastName: String?) {
        sharedPreferences.edit().apply {
            putString("first_name", firstName)
            putString("last_name", lastName)
            commit()
        }
    }

    fun getFirstName(): String {
        return sharedPreferences.getString("first_name", null).toString()
    }

    fun getLastName(): String {
        return sharedPreferences.getString("last_name", null).toString()
    }

    fun saveUserId(id: Int) {
        sharedPreferences.edit().putInt("user_id", id).apply()
    }

    fun getUserId(): Int{
        return sharedPreferences.getInt("user_id",-1)
    }
}