package com.example.challengme.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

// El servidor espera PascalCase para este endpoint
data class RegisterRequest(
    @SerializedName("Email")         val email:         String,
    @SerializedName("Password")      val password:      String,
    @SerializedName("NombreUsuario") val nombreUsuario: String
)