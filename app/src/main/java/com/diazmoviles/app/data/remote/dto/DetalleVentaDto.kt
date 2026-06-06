package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DetalleVentaDto(
    val id: Int,
    val venta: Int,
    val producto: Int,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: String,
    val subtotal: String,
    @SerializedName("producto_nombre") val productoNombre: String
)
