package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.remote.api.ClienteApi
import com.diazmoviles.app.data.remote.api.CreateClienteRequest
import com.diazmoviles.app.data.remote.dto.ClienteDto
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
        return runCatching {
            val response = clienteApi.crearCliente(
                CreateClienteRequest(nombre, apellido, cedula, email, telefono, direccion)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al registrar cliente: ${response.code()}")
            }
        }
    }

    override suspend fun listarClientes(search: String?): Result<List<Cliente>> {
        return runCatching {
            val response = clienteApi.listarClientes(search = search)
            if (response.isSuccessful) {
                response.body()!!.results.map { it.toDomain() }
            } else {
                throw Exception("Error al listar clientes: ${response.code()}")
            }
        }
    }

    override suspend fun obtenerCliente(id: Int): Result<Cliente> {
        return runCatching {
            val response = clienteApi.obtenerCliente(id)
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al obtener cliente: ${response.code()}")
            }
        }
    }

    override suspend fun actualizarCliente(
        id: Int, nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String
    ): Result<Cliente> {
        return runCatching {
            val response = clienteApi.actualizarCliente(
                id, CreateClienteRequest(nombre, apellido, cedula, email, telefono, direccion)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al actualizar cliente: ${response.code()}")
            }
        }
    }

    override suspend fun eliminarCliente(id: Int): Result<Unit> {
        return runCatching {
            val response = clienteApi.eliminarCliente(id)
            if (!response.isSuccessful) {
                throw Exception("Error al eliminar cliente: ${response.code()}")
            }
        }
    }

    private fun ClienteDto.toDomain() = Cliente(
        id = id, nombre = nombre, apellido = apellido,
        cedula = cedula, email = email, telefono = telefono,
        direccion = direccion, activo = activo
    )
}
