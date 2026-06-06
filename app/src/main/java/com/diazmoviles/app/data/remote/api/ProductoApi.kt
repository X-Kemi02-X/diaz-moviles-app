package com.diazmoviles.app.data.remote.api

import com.diazmoviles.app.data.remote.dto.CreateProductoRequest
import com.diazmoviles.app.data.remote.dto.PaginatedDto
import com.diazmoviles.app.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductoApi {
    @GET("productos/")
    suspend fun listarProductos(
        @Query("search") search: String? = null,
        @Query("marca") marca: Int? = null,
        @Query("categoria") categoria: Int? = null,
        @Query("page") page: Int? = null
    ): Response<PaginatedDto<ProductoDto>>

    @GET("productos/{id}/")
    suspend fun obtenerProducto(@Path("id") id: Int): Response<ProductoDto>

    @POST("productos/")
    suspend fun crearProducto(@Body request: CreateProductoRequest): Response<ProductoDto>

    @PUT("productos/{id}/")
    suspend fun actualizarProducto(
        @Path("id") id: Int,
        @Body request: CreateProductoRequest
    ): Response<ProductoDto>

    @DELETE("productos/{id}/")
    suspend fun eliminarProducto(@Path("id") id: Int): Response<Unit>
}
