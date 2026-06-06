package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.remote.api.RegisterApi
import com.diazmoviles.app.data.remote.api.RegisterRequest
import com.diazmoviles.app.domain.repository.RegisterRepository
import org.json.JSONObject
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val registerApi: RegisterApi
) : RegisterRepository {

    override suspend fun register(
        username: String, password: String, email: String,
        nombre: String, apellido: String, cedula: String,
        telefono: String, direccion: String
    ): Result<Unit> {
        return runCatching {
            val response = registerApi.register(
                RegisterRequest(username, password, email, nombre, apellido, cedula, telefono, direccion)
            )
            if (response.isSuccessful) {
                Unit
            } else {
                val errorBody = response.errorBody()?.string()
                val message = if (errorBody != null) {
                    try {
                        val json = JSONObject(errorBody)
                        val keys = json.keys()
                        val errors = mutableListOf<String>()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = json.get(key)
                            if (value is org.json.JSONArray) {
                                for (i in 0 until value.length()) {
                                    errors.add(value.getString(i))
                                }
                            } else {
                                errors.add(value.toString())
                            }
                        }
                        errors.joinToString("\n")
                    } catch (_: Exception) {
                        "Error al registrarse: ${response.code()}"
                    }
                } else {
                    "Error al registrarse: ${response.code()}"
                }
                throw Exception(message)
            }
        }
    }
}
