package com.example.lumos.domain.entities

import com.google.gson.annotations.SerializedName

data class UserData(
    val id: Int,
    val username: String,
    @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("first_name") val firstName:String,
    @SerializedName("last_name") val lastName:String
)