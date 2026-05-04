package com.example.challengme.data.remote.api

sealed class ApiError(message: String) : Exception(message) {
    /** 401 — credenciales inválidas o token expirado */
    object Unauthorized : ApiError("Correo o contraseña incorrectos.")

    /** 409 — el correo ya existe al registrarse */
    data class Conflict(val msg: String?) : ApiError(msg ?: "El correo ya está registrado.")

    /** 429 — demasiados intentos (rate limit) */
    object RateLimited : ApiError("Demasiados intentos. Espera un momento e inténtalo de nuevo.")

    /** Cualquier otro código de error HTTP */
    data class ServerError(val code: Int, val msg: String?) :
        ApiError("Error del servidor ($code)${msg?.let { ": $it" } ?: ""}.")

    /** Error al deserializar la respuesta del servidor */
    data class DecodingError(val error: Throwable) :
        ApiError("No se pudo procesar la respuesta: ${error.message}")

    /** Error de conectividad o de socket */
    data class NetworkError(val error: Throwable) :
        ApiError("Error de red: ${error.message}")
}