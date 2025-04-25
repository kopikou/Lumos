package com.example.lumos.retrofit

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

//механизм обновления токена
class TokenAuthenticator(private val tokenManager: TokenManager,
                         private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        return runBlocking(coroutineDispatcher) {
            try {
                val newToken = ApiClient.getAuthService().refreshToken(mapOf("refresh" to refreshToken))
                if (newToken.isSuccessful) {
                    newToken.body()?.let {
                        tokenManager.saveTokens(it.access, it.refresh)
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${it.access}")
                            .build()
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}