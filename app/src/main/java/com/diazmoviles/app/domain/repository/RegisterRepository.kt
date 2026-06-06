package com.diazmoviles.app.domain.repository

interface RegisterRepository {
    suspend fun register(
        username: String, password: String, email: String,
        nombre: String, apellido: String, cedula: String,
        telefono: String, direccion: String
    ): Result<Unit>
}
