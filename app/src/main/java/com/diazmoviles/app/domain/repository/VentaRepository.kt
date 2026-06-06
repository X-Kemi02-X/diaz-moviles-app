package com.diazmoviles.app.domain.repository

import com.diazmoviles.app.domain.model.Venta

interface VentaRepository {
    suspend fun crearVenta(
        clienteId: Int, metodoPago: String, observacion: String
    ): Result<Venta>

    suspend fun crearDetalle(
        ventaId: Int, productoId: Int, cantidad: Int, precioUnitario: String
    ): Result<Unit>

    suspend fun listarVentas(page: Int? = null, usuarioId: Int? = null): Result<List<Venta>>

    suspend fun obtenerVenta(id: Int): Result<Venta>

    suspend fun actualizarEstadoVenta(id: Int, estado: String): Result<Venta>

    suspend fun eliminarVenta(id: Int): Result<Unit>
}
