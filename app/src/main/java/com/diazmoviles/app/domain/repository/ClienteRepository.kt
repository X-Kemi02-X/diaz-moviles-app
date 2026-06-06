package com.diazmoviles.app.domain.repository

import com.diazmoviles.app.domain.model.Cliente

interface ClienteRepository {
    suspend fun crearCliente(
        nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String
    ): Result<Cliente>

    suspend fun listarClientes(): Result<List<Cliente>>

    suspend fun obtenerCliente(id: Int): Result<Cliente>

    suspend fun actualizarCliente(
        id: Int, nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String
    ): Result<Cliente>

    suspend fun eliminarCliente(id: Int): Result<Unit>
}
