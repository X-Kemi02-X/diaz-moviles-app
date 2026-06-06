package com.diazmoviles.app.data.remote.util

import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Response<*>.parseError(): String {
    val code = code()
    val errorBody = errorBody()?.string()
    val serverMsg = errorBody?.let { parseServerError(it) }

    if (serverMsg != null) return serverMsg

    return when (code) {
        400 -> "Datos inválidos. Verifica los campos e intenta de nuevo."
        401 -> "Usuario no autorizado. Inicia sesión nuevamente."
        403 -> "No tienes permisos para realizar esta acción."
        404 -> "Registro no encontrado."
        500 -> "Error del servidor. Intenta más tarde."
        else -> "Error $code"
    }
}

private fun parseServerError(body: String): String? {
    return try {
        val json = JSONObject(body)

        if (json.has("detail")) return json.getString("detail")

        if (json.length() > 0) {
            val keys = json.keys()
            val messages = mutableListOf<String>()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = json.get(key)
                when (value) {
                    is String -> messages.add(value)
                    is JSONArray -> {
                        for (i in 0 until value.length()) {
                            messages.add(value.getString(i))
                        }
                    }
                }
            }
            if (messages.isNotEmpty()) return messages.joinToString("\n")
        }

        null
    } catch (_: Exception) {
        null
    }
}

fun Exception.toUserMessage(): String {
    return when (this) {
        is UnknownHostException -> "Sin conexión a internet. Verifica tu conexión."
        is ConnectException -> "No se pudo conectar al servidor. Intenta de nuevo."
        is SocketTimeoutException -> "El servidor no responde. Verifica tu conexión."
        else -> message ?: "Error inesperado"
    }
}

suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(Exception(e.toUserMessage()))
    }
}
