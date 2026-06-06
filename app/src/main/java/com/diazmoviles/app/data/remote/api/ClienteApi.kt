package com.diazmoviles.app.data.remote.api

import com.diazmoviles.app.data.remote.dto.ClienteDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateClienteRequest(
    val nombre: String,
    val apellido: String,
    val cedula: String,
    val email: String,
    val telefono: String,
    val direccion: String
)

interface ClienteApi {
    @POST("clientes/")
    suspend fun crearCliente(@Body request: CreateClienteRequest): Response<ClienteDto>

    @GET("clientes/")
    suspend fun listarClientes(): Response<List<ClienteDto>>

    @GET("clientes/{id}/")
    suspend fun obtenerCliente(@Path("id") id: Int): Response<ClienteDto>

    @PUT("clientes/{id}/")
    suspend fun actualizarCliente(
        @Path("id") id: Int,
        @Body request: CreateClienteRequest
    ): Response<ClienteDto>

    @DELETE("clientes/{id}/")
    suspend fun eliminarCliente(@Path("id") id: Int): Response<Unit>
}
