package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.remote.api.CreateProveedorRequest
import com.diazmoviles.app.data.remote.api.ProveedorApi
import com.diazmoviles.app.data.remote.dto.ProveedorDto
import com.diazmoviles.app.data.remote.util.parseError
import com.diazmoviles.app.data.remote.util.safeApiCall
import com.diazmoviles.app.domain.model.Proveedor
import com.diazmoviles.app.domain.repository.ProveedorRepository
import javax.inject.Inject

class ProveedorRepositoryImpl @Inject constructor(
    private val proveedorApi: ProveedorApi
) : ProveedorRepository {

    override suspend fun listarProveedores(): Result<List<Proveedor>> {
        return safeApiCall {
            val response = proveedorApi.listarProveedores()
            if (response.isSuccessful) {
                response.body()!!.map { it.toDomain() }
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun obtenerProveedor(id: Int): Result<Proveedor> {
        return safeApiCall {
            val response = proveedorApi.obtenerProveedor(id)
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun crearProveedor(
        nombre: String, contacto: String, telefono: String,
        email: String, direccion: String
    ): Result<Proveedor> {
        return safeApiCall {
            val response = proveedorApi.crearProveedor(
                CreateProveedorRequest(nombre, contacto, telefono, email, direccion)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun actualizarProveedor(
        id: Int, nombre: String, contacto: String, telefono: String,
        email: String, direccion: String
    ): Result<Proveedor> {
        return safeApiCall {
            val response = proveedorApi.actualizarProveedor(
                id, CreateProveedorRequest(nombre, contacto, telefono, email, direccion)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun eliminarProveedor(id: Int): Result<Unit> {
        return safeApiCall {
            val response = proveedorApi.eliminarProveedor(id)
            if (!response.isSuccessful) {
                throw Exception(response.parseError())
            }
        }
    }

    private fun ProveedorDto.toDomain() = Proveedor(
        id = id, nombre = nombre, contacto = contacto,
        telefono = telefono, email = email, direccion = direccion,
        activo = activo
    )
}
