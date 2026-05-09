package com.example.challengme.data.services

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
import com.example.challengme.data.remote.api.ApiError
import com.example.challengme.data.remote.dto.API.constant.EndpointConstant
import com.example.challengme.data.remote.dto.auth.shipment.AuthShipment
import com.example.challengme.data.remote.dto.auth.request.LoginRequest
import com.example.challengme.data.remote.dto.auth.request.RecuperarPasswordRequest
import com.example.challengme.data.remote.dto.auth.request.RegisterRequest

class AuthService private constructor(
    private val apiClient:   ApiClient,
    private val authManager: AuthManager
) {

    // ── Singleton ────────────────────────────────────────────
    companion object {
        val shared: AuthService by lazy {
            AuthService(ApiClient.shared, AuthManager.shared)
        }
    }

    // ── Login con correo y contraseña ─────────────────────────
    // Lanza ApiError si el servidor devuelve un error.
    suspend fun loginEmail(email: String, password: String): AuthShipment {
        val response = apiClient.send<AuthShipment>(
            path   = EndpointConstant.Endpoint.AUTH_LOGIN_EMAIL,
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
    ): AuthShipment {
        val response = apiClient.send<AuthShipment>(
            path   = EndpointConstant.Endpoint.AUTH_REGISTRO,
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

    // ── Recuperar contraseña ──────────────────────────────────
    // Lanza Exception con mensaje amigable si el servidor falla (5xx).
    // El servidor devuelve 200 aunque el email no exista (user enumeration prevention).
    suspend fun recuperarPassword(email: String) {
        try {
            apiClient.sendVoid(
                path   = EndpointConstant.Endpoint.AUTH_RECUPERAR_PASSWORD,
                method = "POST",
                body   = RecuperarPasswordRequest(email = email)
            )
        } catch (e: ApiError.ServerError) {
            throw Exception("Error del servidor. Inténtalo más tarde.")
        }
    }

    // ── Cerrar sesión ─────────────────────────────────────────
    // Notifica al servidor (ignorando errores) y limpia el token local.
    suspend fun logout() {
        try {
            apiClient.sendVoid(EndpointConstant.Endpoint.AUTH_LOGOUT, "POST")
        } catch (_: Exception) {
            // El servidor puede estar caído o el token ya expirado;
            // en cualquier caso limpiamos la sesión local.
        }
        authManager.logout()
    }
}
