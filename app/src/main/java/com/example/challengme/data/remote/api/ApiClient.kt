package com.example.challengme.data.remote.api

// ============================================================
//  ApiClient.kt
//  ChallengMe
//
//  • Inyecta el JWT Bearer automáticamente en cada petición
//    leyéndolo de AuthManager (equivalente a AuthTokenHandler).
//  • Si el servidor devuelve 401 → hace logout automático.
//  • Expone send<T> (con respuesta), sendVoid (sin respuesta)
//    y upload (multipart/form-data).
// ============================================================

import com.example.challengme.data.local.AuthManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

// ── Errores de red ────────────────────────────────────────────

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

// ── ApiClient ─────────────────────────────────────────────────

class ApiClient private constructor(private val auth: AuthManager) {

    // ── Singleton ────────────────────────────────────────────
    companion object {
        // Se inicializa la primera vez que se accede; requiere que
        // AuthManager.init(context) haya sido llamado antes.
        val shared: ApiClient by lazy { ApiClient(AuthManager.shared) }
    }

    // Gson con snake_case ↔ camelCase automático
    private val gson: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    // ── Petición con respuesta decodificable ──────────────────
    // Uso: val sesion: SesionDto = ApiClient.shared.send(ApiConfig.Endpoint.AUTH_LOGIN_EMAIL, "POST", body)
    suspend inline fun <reified T> send(
        path:   String,
        method: String = "GET",
        body:   Any?   = null
    ): T = sendWithType(path, method, body, T::class.java)

    // Implementación interna tipada — @PublishedApi permite que
    // la función inline anterior la llame sin violar visibilidad.
    @PublishedApi
    internal suspend fun <T> sendWithType(
        path:   String,
        method: String,
        body:   Any?,
        type:   Class<T>
    ): T = withContext(Dispatchers.IO) {
        val request = buildRequest(path, method, body)
        val (code, responseBody) = performRequest(request)
        validateStatus(code, responseBody)
        try {
            gson.fromJson(responseBody, type)
                ?: throw ApiError.DecodingError(NullPointerException("Respuesta vacía del servidor"))
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw ApiError.DecodingError(e)
        }
    }

    // ── Petición sin respuesta (201 Created, 204 No Content…) ─
    // Uso: ApiClient.shared.sendVoid(ApiConfig.Endpoint.AUTH_LOGOUT, "POST")
    suspend fun sendVoid(
        path:   String,
        method: String = "POST",
        body:   Any?   = null
    ) = withContext(Dispatchers.IO) {
        val request = buildRequest(path, method, body)
        val (code, responseBody) = performRequest(request)
        validateStatus(code, responseBody)
    }

    // ── Subida de archivo (evidencias / avatar) ───────────────
    // Sube datos binarios con multipart/form-data.
    // Uso: ApiClient.shared.upload(imageBytes, "image/jpeg",
    //          path = ApiConfig.Endpoint.evidence(retoId))
    suspend fun upload(
        fileData:  ByteArray,
        mimeType:  String,
        fieldName: String = "file",
        path:      String,
        method:    String = "POST"
    ) = withContext(Dispatchers.IO) {
        val token = auth.token()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(fieldName, "upload", fileData.toRequestBody(mimeType.toMediaType()))
            .build()
        val request = Request.Builder()
            .url(ApiConfig.buildUrl(path))
            .method(method.uppercase(), requestBody)
            .apply {
                token?.let { header(ApiConfig.JWT.HEADER_KEY, "${ApiConfig.JWT.HEADER_PREFIX} $it") }
            }
            .build()
        val (code, responseBody) = performRequest(request)
        validateStatus(code, responseBody)
    }

    // ── Internos ──────────────────────────────────────────────

    private fun buildRequest(path: String, method: String, body: Any?): Request {
        val url   = ApiConfig.buildUrl(path)
        val token = auth.token()

        val reqBody: RequestBody? = body?.let {
            gson.toJson(it).toRequestBody("application/json".toMediaType())
        }

        val builder = Request.Builder().url(url)
        when (method.uppercase()) {
            "GET"    -> builder.get()
            "POST"   -> builder.post(reqBody ?: "{}".toRequestBody("application/json".toMediaType()))
            "PUT"    -> builder.put(reqBody ?: "{}".toRequestBody("application/json".toMediaType()))
            "PATCH"  -> builder.patch(reqBody ?: "{}".toRequestBody("application/json".toMediaType()))
            "DELETE" -> if (reqBody != null) builder.delete(reqBody) else builder.delete()
            else     -> builder.method(method.uppercase(), reqBody)
        }

        ApiConfig.COMMON_HEADERS.forEach { (key, value) -> builder.header(key, value) }
        token?.let { builder.header(ApiConfig.JWT.HEADER_KEY, "${ApiConfig.JWT.HEADER_PREFIX} $it") }

        return builder.build()
    }

    private fun performRequest(request: Request): Pair<Int, String> {
        return try {
            client.newCall(request).execute().use { response ->
                Pair(response.code, response.body?.string() ?: "")
            }
        } catch (e: IOException) {
            throw ApiError.NetworkError(e)
        }
    }

    private fun validateStatus(code: Int, body: String) {
        when (code) {
            in 200..299 -> return
            401 -> {
                // Token expirado o inválido — cierra la sesión y propaga el error
                auth.logout()
                throw ApiError.Unauthorized
            }
            409 -> throw ApiError.Conflict(body.ifBlank { null })
            429 -> throw ApiError.RateLimited
            else -> throw ApiError.ServerError(code, body.ifBlank { null })
        }
    }
}
