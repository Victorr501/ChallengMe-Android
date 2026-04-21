package com.example.challengme.data.local

// ============================================================
//  AuthManager.kt
//  ChallengMe
//
//  Equivalente a TokenStore + JwtAuthStateProvider de .NET.
//
//  • Guarda el JWT en EncryptedSharedPreferences
//    (equivalente a Keychain en iOS).
//  • Parsea los claims sin librerías externas
//    (Base64 + JSONObject de Android).
//  • Expone StateFlow<Boolean> para que las vistas reaccionen
//    al estado de autenticación (equivalente a @Published).
//  • Hace logout automático si el token está expirado al arrancar.
// ============================================================

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.util.Date

class AuthManager private constructor(context: Context) {

    // ── Singleton ────────────────────────────────────────────
    companion object {
        @Volatile private var instance: AuthManager? = null

        // Llama a este método en Application.onCreate() antes de usar AuthManager.shared.
        fun init(context: Context): AuthManager =
            instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }

        val shared: AuthManager
            get() = requireNotNull(instance) {
                "AuthManager.init(context) no ha sido llamado. Llámalo en Application.onCreate()."
            }
    }

    // ── EncryptedSharedPreferences (equivalente a Keychain) ───
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "challengeme_secure_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val TOKEN_KEY = "com.challengeme.jwt"

    // ── Estado observable ─────────────────────────────────────
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _claims = MutableStateFlow<JwtClaims?>(null)
    val claims: StateFlow<JwtClaims?> = _claims.asStateFlow()

    // ── Init: restaura sesión al arrancar ─────────────────────
    init {
        val saved = prefs.getString(TOKEN_KEY, null)
        when {
            saved != null && !isExpired(saved) -> {
                _isAuthenticated.value = true
                _claims.value = parseClaims(saved)
            }
            saved != null -> logout() // token guardado pero expirado → limpia
        }
    }

    // ── API pública ───────────────────────────────────────────

    // Llama a este método tras recibir el JWT del servidor.
    fun setToken(token: String) {
        if (isExpired(token)) { logout(); return }
        prefs.edit().putString(TOKEN_KEY, token).apply()
        _claims.value = parseClaims(token)
        _isAuthenticated.value = true
    }

    // Cierra la sesión y borra el token del almacenamiento seguro.
    fun logout() {
        prefs.edit().remove(TOKEN_KEY).apply()
        _claims.value = null
        _isAuthenticated.value = false
    }

    // Devuelve el token vigente o null si expiró / no existe.
    // Si está expirado, también hace logout automáticamente.
    fun token(): String? {
        val t = prefs.getString(TOKEN_KEY, null) ?: return null
        if (isExpired(t)) { logout(); return null }
        return t
    }

    // Comprueba si el campo "exp" del JWT es anterior a ahora.
    fun isExpired(token: String): Boolean {
        val payload = decodePayload(token) ?: return true
        val exp = payload.optLong("exp", -1L)
        if (exp == -1L) return true
        return Date(exp * 1000L).before(Date())
    }

    // ── JWT: parseo de claims ─────────────────────────────────

    private fun parseClaims(token: String): JwtClaims? {
        val payload = decodePayload(token) ?: return null

        // "roles" puede llegar como JSONArray o como String único
        val roles: List<String> = run {
            val arr = payload.optJSONArray("roles") ?: payload.optJSONArray("role")
            when {
                arr != null          -> (0 until arr.length()).map { arr.getString(it) }
                payload.has("roles") -> listOf(payload.getString("roles"))
                payload.has("role")  -> listOf(payload.getString("role"))
                else                 -> emptyList()
            }
        }

        return JwtClaims(
            // busca primero el nombre del servidor y luego el claim estándar JWT
            id            = (payload.opt("id")     as? String) ?: (payload.opt("sub")   as? String),
            nombreUsuario = (payload.opt("nombre")  as? String) ?: (payload.opt("name")  as? String),
            correo        = (payload.opt("correo")  as? String) ?: (payload.opt("email") as? String),
            issuer        = payload.opt("iss") as? String,
            audience      = payload.opt("aud") as? String,
            expiresAt     = if (payload.has("exp")) Date(payload.getLong("exp") * 1000L) else null,
            issuedAt      = if (payload.has("iat")) Date(payload.getLong("iat") * 1000L) else null,
            roles         = roles
        )
    }

    // Decodifica el payload (parte central) del JWT sin librerías externas.
    // Formato JWT: header.payload.signature — todo en Base64URL.
    private fun decodePayload(token: String): JSONObject? {
        val parts = token.split(".")
        if (parts.size != 3) return null

        // Base64URL → Base64 estándar
        var base64 = parts[1]
            .replace("-", "+")
            .replace("_", "/")

        // Rellena hasta múltiplo de 4
        val remainder = base64.length % 4
        if (remainder != 0) base64 += "=".repeat(4 - remainder)

        return try {
            val decoded = Base64.decode(base64, Base64.DEFAULT)
            JSONObject(String(decoded, Charsets.UTF_8))
        } catch (e: Exception) {
            null
        }
    }
}

// ── JwtClaims ─────────────────────────────────────────────────
// Issuer: "challengeme-api" | Audience: "challengeme-app"
data class JwtClaims(
    val id:            String?,      // claim "id" o "sub"
    val nombreUsuario: String?,      // claim "nombre" o "name"
    val correo:        String?,      // claim "correo" o "email"
    val roles:         List<String>, // claim "roles" o "role"
    val issuer:        String?,      // claim "iss" → "challengeme-api"
    val audience:      String?,      // claim "aud" → "challengeme-app"
    val expiresAt:     Date?,        // claim "exp"
    val issuedAt:      Date?         // claim "iat"
)
