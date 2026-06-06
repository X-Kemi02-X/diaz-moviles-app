package com.diazmoviles.app.data.remote.api

import com.diazmoviles.app.data.remote.dto.MarcaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateMarcaRequest(
    val nombre: String,
    val descripcion: String = ""
)

interface MarcaApi {
    @GET("marcas/")
    suspend fun listarMarcas(): Response<List<MarcaDto>>

    @POST("marcas/")
    suspend fun crearMarca(@Body request: CreateMarcaRequest): Response<MarcaDto>

    @PUT("marcas/{id}/")
    suspend fun actualizarMarca(
        @Path("id") id: Int,
        @Body request: CreateMarcaRequest
    ): Response<MarcaDto>

    @DELETE("marcas/{id}/")
    suspend fun eliminarMarca(@Path("id") id: Int): Response<Unit>
}
