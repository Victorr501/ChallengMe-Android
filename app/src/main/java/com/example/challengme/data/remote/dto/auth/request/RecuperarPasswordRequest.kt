package com.example.challengme.data.remote.dto.auth.request

import com.google.gson.annotations.SerializedName

data class RecuperarPasswordRequest(
    @SerializedName("email") val email: String
)
