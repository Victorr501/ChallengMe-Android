package com.example.challengme.data.remote.dto.API.constant;

object EndpointConstant {
    // ── Endpoints ────────────────────────────────────────────
    object Endpoint {

        // Auth
        const val AUTH_LOGIN_EMAIL        = "/auth/login-email"
        const val AUTH_REGISTRO           = "/auth/registro"
        const val AUTH_REFRESH            = "/auth/refresh"
        const val AUTH_LOGOUT             = "/auth/logout"
        const val AUTH_RECUPERAR_PASSWORD = "/auth/recuperar-password"

        // Ranking
        const val LEADERBOARD = "/leaderboard"

        // Evidencias — ruta dinámica por reto
        fun evidence(challengeId: String) = "/challenges/$challengeId/evidence"
    }
}
