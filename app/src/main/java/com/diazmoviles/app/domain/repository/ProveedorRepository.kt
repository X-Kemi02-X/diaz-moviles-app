package com.diazmoviles.app.domain.repository

import com.diazmoviles.app.domain.model.Proveedor

interface ProveedorRepository {
    suspend fun listarProveedores(): Result<List<Proveedor>>
    suspend fun obtenerProveedor(id: Int): Result<Proveedor>
    suspend fun crearProveedor(
        nombre: String, contacto: String, telefono: String,
        email: String, direccion: String
    ): Result<Proveedor>
    suspend fun actualizarProveedor(
        id: Int, nombre: String, contacto: String, telefono: String,
        email: String, direccion: String
    ): Result<Proveedor>
    suspend fun eliminarProveedor(id: Int): Result<Unit>
}
