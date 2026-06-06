package com.diazmoviles.app.data.remote.api

import com.diazmoviles.app.data.remote.dto.CreateDetalleVentaRequest
import com.diazmoviles.app.data.remote.dto.CreateVentaRequest
import com.diazmoviles.app.data.remote.dto.DetalleVentaDto
import com.diazmoviles.app.data.remote.dto.PaginatedDto
import com.diazmoviles.app.data.remote.dto.UpdateVentaEstadoRequest
import com.diazmoviles.app.data.remote.dto.VentaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VentaApi {
    @POST("ventas/")
    suspend fun crearVenta(@Body request: CreateVentaRequest): Response<VentaDto>

    @GET("ventas/")
    suspend fun listarVentas(
        @Query("page") page: Int? = null,
        @Query("usuario") usuario: Int? = null
    ): Response<PaginatedDto<VentaDto>>

    @GET("ventas/{id}/")
    suspend fun obtenerVenta(
        @Path("id") id: Int
    ): Response<VentaDto>

    @PUT("ventas/{id}/")
    suspend fun actualizarVenta(
        @Path("id") id: Int,
        @Body request: CreateVentaRequest
    ): Response<VentaDto>

    @PATCH("ventas/{id}/")
    suspend fun actualizarEstadoVenta(
        @Path("id") id: Int,
        @Body request: UpdateVentaEstadoRequest
    ): Response<VentaDto>

    @DELETE("ventas/{id}/")
    suspend fun eliminarVenta(@Path("id") id: Int): Response<Unit>

    @POST("detalles-venta/")
    suspend fun crearDetalle(@Body request: CreateDetalleVentaRequest): Response<DetalleVentaDto>
}
