package com.example.lumos.domain.entities

import com.google.gson.annotations.SerializedName

data class UserUpdateRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)