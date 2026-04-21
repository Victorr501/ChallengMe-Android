package com.example.challengme.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String
)