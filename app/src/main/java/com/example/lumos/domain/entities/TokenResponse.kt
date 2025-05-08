package com.example.lumos.domain.entities

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    val access: String,
    val refresh: String,
    val user: UserData
)

