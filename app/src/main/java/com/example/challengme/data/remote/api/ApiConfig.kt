package com.example.challengme.data.remote.api

// ============================================================
//  ApiConfig.kt
//  ChallengMe
//
//  Configuración central del servidor.
//  Los secretos (SecretKey, TenantId, ClientId, ClientSecret)
//  viven SOLO en el backend — nunca se envían al cliente.
// ============================================================

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

object ApiConfig {

    // ── Base URL ─────────────────────────────────────────────
    const val BASE_URL = "https://api-challengeme-ddcpawg6ama0cncn.spaincentral-01.azurewebsites.net/api"

    // ── Timeout (segundos) ───────────────────────────────────
    const val TIMEOUT_SECONDS = 30L

    // ── Headers comunes ──────────────────────────────────────
    val COMMON_HEADERS = mapOf(
        "Content-Type" to "application/json",
        "Accept"       to "application/json"
    )

    // ── JWT ──────────────────────────────────────────────────
    object JWT {
        const val ISSUER           = "challengeme-api"
        const val AUDIENCE         = "challengeme-app"
        const val EXPIRATION_HOURS = 24               // el token dura 24 h
        const val HEADER_KEY       = "Authorization"
        const val HEADER_PREFIX    = "Bearer"         // "Bearer <token>"
    }

    // ── Blob Storage ─────────────────────────────────────────
    // Contenedor donde el backend almacena las evidencias
    // (fotos/vídeos) que el usuario sube al completar un reto.
    object BlobStorage {
        const val CONTAINER_NAME = "evidencias"
    }

    // ── Endpoints ────────────────────────────────────────────
    object Endpoint {

        // Auth
        const val AUTH_LOGIN_EMAIL = "/auth/login-email"
        const val AUTH_REGISTRO    = "/auth/registro"
        const val AUTH_REFRESH     = "/auth/refresh"
        const val AUTH_LOGOUT      = "/auth/logout"

        // Ranking
        const val LEADERBOARD = "/leaderboard"

        // Evidencias — ruta dinámica por reto
        fun evidence(challengeId: String) = "/challenges/$challengeId/evidence"
    }

    // ── Constructor de URL ────────────────────────────────────
    // Construye una HttpUrl de OkHttp concatenando BASE_URL + path.
    // Lanza IllegalArgumentException si la URL resultante es inválida.
    fun buildUrl(path: String): HttpUrl = (BASE_URL + path).toHttpUrl()
}
