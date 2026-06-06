package com.diazmoviles.app.data.remote.api

import com.diazmoviles.app.data.remote.dto.CategoriaDto
import com.diazmoviles.app.data.remote.dto.PaginatedDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateCategoriaRequest(
    val nombre: String,
    val descripcion: String = ""
)

interface CategoriaApi {
    @GET("categorias/")
    suspend fun listarCategorias(): Response<PaginatedDto<CategoriaDto>>

    @POST("categorias/")
    suspend fun crearCategoria(@Body request: CreateCategoriaRequest): Response<CategoriaDto>

    @PUT("categorias/{id}/")
    suspend fun actualizarCategoria(
        @Path("id") id: Int,
        @Body request: CreateCategoriaRequest
    ): Response<CategoriaDto>

    @DELETE("categorias/{id}/")
    suspend fun eliminarCategoria(@Path("id") id: Int): Response<Unit>
}
