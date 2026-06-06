package com.diazmoviles.app.data.remote.api

import com.diazmoviles.app.data.remote.dto.ProveedorDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateProveedorRequest(
    val nombre: String,
    val contacto: String = "",
    val telefono: String = "",
    val email: String = "",
    val direccion: String = "",
    val activo: Boolean = true
)

interface ProveedorApi {
    @GET("proveedores/")
    suspend fun listarProveedores(): Response<List<ProveedorDto>>

    @GET("proveedores/{id}/")
    suspend fun obtenerProveedor(@Path("id") id: Int): Response<ProveedorDto>

    @POST("proveedores/")
    suspend fun crearProveedor(@Body request: CreateProveedorRequest): Response<ProveedorDto>

    @PUT("proveedores/{id}/")
    suspend fun actualizarProveedor(
        @Path("id") id: Int,
        @Body request: CreateProveedorRequest
    ): Response<ProveedorDto>

    @DELETE("proveedores/{id}/")
    suspend fun eliminarProveedor(@Path("id") id: Int): Response<Unit>
}
