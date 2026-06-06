package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.remote.api.VentaApi
import com.diazmoviles.app.data.remote.dto.CreateDetalleVentaRequest
import com.diazmoviles.app.data.remote.dto.CreateVentaRequest
import com.diazmoviles.app.data.remote.dto.DetalleVentaDto
import com.diazmoviles.app.data.remote.dto.VentaDto
import com.diazmoviles.app.domain.model.DetalleVenta
import com.diazmoviles.app.domain.model.Venta
import com.diazmoviles.app.domain.repository.VentaRepository
import javax.inject.Inject

class VentaRepositoryImpl @Inject constructor(
    private val ventaApi: VentaApi
) : VentaRepository {

    override suspend fun crearVenta(
        clienteId: Int, metodoPago: String, observacion: String
    ): Result<Venta> {
        return runCatching {
            val response = ventaApi.crearVenta(
                CreateVentaRequest(cliente = clienteId, metodoPago = metodoPago, observacion = observacion)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al crear venta: ${response.code()}")
            }
        }
    }

    override suspend fun crearDetalle(
        ventaId: Int, productoId: Int, cantidad: Int, precioUnitario: String
    ): Result<Unit> {
        return runCatching {
            val response = ventaApi.crearDetalle(
                CreateDetalleVentaRequest(venta = ventaId, producto = productoId, cantidad = cantidad, precioUnitario = precioUnitario)
            )
            if (!response.isSuccessful) {
                throw Exception("Error al crear detalle: ${response.code()}")
            }
        }
    }

    override suspend fun listarVentas(page: Int?, usuarioId: Int?): Result<List<Venta>> {
        return runCatching {
            val response = ventaApi.listarVentas(page = page, usuario = usuarioId)
            if (response.isSuccessful) {
                response.body()!!.results.map { it.toDomain() }
            } else {
                throw Exception("Error al cargar ventas: ${response.code()}")
            }
        }
    }

    override suspend fun obtenerVenta(id: Int): Result<Venta> {
        return runCatching {
            val response = ventaApi.obtenerVenta(id)
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al obtener venta: ${response.code()}")
            }
        }
    }

    override suspend fun eliminarVenta(id: Int): Result<Unit> {
        return runCatching {
            val response = ventaApi.eliminarVenta(id)
            if (!response.isSuccessful) {
                throw Exception("Error al eliminar venta: ${response.code()}")
            }
        }
    }

    private fun VentaDto.toDomain() = Venta(
        id = id,
        clienteId = cliente,
        clienteNombre = clienteNombre,
        usuarioNombre = usuarioNombre,
        fecha = fecha,
        total = total,
        estado = estado,
        metodoPago = metodoPago,
        observacion = observacion,
        detalles = detalles.map { it.toDomain() }
    )

    private fun DetalleVentaDto.toDomain() = DetalleVenta(
        id = id,
        productoId = producto,
        productoNombre = productoNombre,
        cantidad = cantidad,
        precioUnitario = precioUnitario,
        subtotal = subtotal
    )
}
