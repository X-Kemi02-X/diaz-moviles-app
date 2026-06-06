package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.remote.api.ClienteApi
import com.diazmoviles.app.data.remote.api.CreateClienteRequest
import com.diazmoviles.app.data.remote.dto.ClienteDto
import com.diazmoviles.app.data.remote.util.parseError
import com.diazmoviles.app.data.remote.util.safeApiCall
import com.diazmoviles.app.domain.model.Cliente
import com.diazmoviles.app.domain.repository.ClienteRepository
import javax.inject.Inject

class ClienteRepositoryImpl @Inject constructor(
    private val clienteApi: ClienteApi
) : ClienteRepository {

    override suspend fun crearCliente(
        nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String
    ): Result<Cliente> {
        return safeApiCall {
            val response = clienteApi.crearCliente(
                CreateClienteRequest(nombre, apellido, cedula, email, telefono, direccion)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun listarClientes(search: String?): Result<List<Cliente>> {
        return safeApiCall {
            val response = clienteApi.listarClientes(search = search)
            if (response.isSuccessful) {
                response.body()!!.results.map { it.toDomain() }
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun obtenerCliente(id: Int): Result<Cliente> {
        return safeApiCall {
            val response = clienteApi.obtenerCliente(id)
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun actualizarCliente(
        id: Int, nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String
    ): Result<Cliente> {
        return safeApiCall {
            val response = clienteApi.actualizarCliente(
                id, CreateClienteRequest(nombre, apellido, cedula, email, telefono, direccion)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception(response.parseError())
            }
        }
    }

    override suspend fun eliminarCliente(id: Int): Result<Unit> {
        return safeApiCall {
            val response = clienteApi.eliminarCliente(id)
            if (!response.isSuccessful) {
                throw Exception(response.parseError())
            }
        }
    }

    private fun ClienteDto.toDomain() = Cliente(
        id = id, nombre = nombre, apellido = apellido,
        cedula = cedula, email = email, telefono = telefono,
        direccion = direccion, activo = activo
    )
}
