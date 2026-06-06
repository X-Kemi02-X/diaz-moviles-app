package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.remote.api.RegisterApi
import com.diazmoviles.app.data.remote.api.RegisterRequest
import com.diazmoviles.app.domain.repository.RegisterRepository
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
            if (!response.isSuccessful) {
                throw Exception("Error al registrarse: ${response.code()}")
            }
        }
    }
}
