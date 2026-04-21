package com.example.challengme.data.remote.repository

// ============================================================
//  AuthRepository.kt
//  ChallengMe
//
//  Equivalente a AuthService.swift.
//  Orquesta las peticiones de autenticación: delega la red a
//  ApiClient y la gestión del token a AuthManager.
// ============================================================

import com.example.challengme.data.local.AuthManager
import com.example.challengme.data.remote.api.ApiClient
import com.example.challengme.data.remote.api.ApiConfig
import com.example.challengme.data.remote.dto.auth.AuthResponse
import com.example.challengme.data.remote.dto.auth.LoginRequest
import com.example.challengme.data.remote.dto.auth.RegisterRequest

class AuthRepository private constructor(
    private val apiClient:   ApiClient,
    private val authManager: AuthManager
) {

    // ── Singleton ────────────────────────────────────────────
    companion object {
        val shared: AuthRepository by lazy {
            AuthRepository(ApiClient.shared, AuthManager.shared)
        }
    }

    // ── Login con correo y contraseña ─────────────────────────
    // Lanza ApiError si el servidor devuelve un error.
    suspend fun loginEmail(email: String, password: String): AuthResponse {
        val response = apiClient.send<AuthResponse>(
            path   = ApiConfig.Endpoint.AUTH_LOGIN_EMAIL,
            method = "POST",
            body   = LoginRequest(email = email, password = password)
        )
        authManager.setToken(response.token)
        return response
    }

    // ── Registro de nueva cuenta ──────────────────────────────
    // Lanza ApiError si el correo ya existe (409) u otro fallo.
    suspend fun register(
        email:         String,
        password:      String,
        nombreUsuario: String
    ): AuthResponse {
        val response = apiClient.send<AuthResponse>(
            path   = ApiConfig.Endpoint.AUTH_REGISTRO,
            method = "POST",
            body   = RegisterRequest(
                email         = email,
                password      = password,
                nombreUsuario = nombreUsuario
            )
        )
        authManager.setToken(response.token)
        return response
    }

    // ── Cerrar sesión ─────────────────────────────────────────
    // Notifica al servidor (ignorando errores) y limpia el token local.
    suspend fun logout() {
        try {
            apiClient.sendVoid(ApiConfig.Endpoint.AUTH_LOGOUT, "POST")
        } catch (_: Exception) {
            // El servidor puede estar caído o el token ya expirado;
            // en cualquier caso limpiamos la sesión local.
        }
        authManager.logout()
    }
}
