package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VentaDto(
    val id: Int,
    val cliente: Int,
    val usuario: Int,
    val fecha: String,
    val total: String,
    val estado: String,
    @SerializedName("metodo_pago") val metodoPago: String,
    val observacion: String,
    @SerializedName("fecha_actualizacion") val fechaActualizacion: String,
    val detalles: List<DetalleVentaDto> = emptyList(),
    @SerializedName("cliente_nombre") val clienteNombre: String?,
    @SerializedName("usuario_nombre") val usuarioNombre: String?
)

data class CreateVentaRequest(
    val cliente: Int,
    @SerializedName("metodo_pago") val metodoPago: String = "efectivo",
    val observacion: String = ""
)

data class UpdateVentaEstadoRequest(
    val estado: String
)

data class CreateDetalleVentaRequest(
    val venta: Int,
    val producto: Int,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: String
)
