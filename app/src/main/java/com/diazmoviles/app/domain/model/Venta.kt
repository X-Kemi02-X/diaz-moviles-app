package com.diazmoviles.app.domain.model

data class Venta(
    val id: Int,
    val clienteId: Int,
    val clienteNombre: String?,
    val usuarioNombre: String?,
    val fecha: String,
    val total: String,
    val estado: String,
    val metodoPago: String,
    val observacion: String,
    val detalles: List<DetalleVenta>
)

data class DetalleVenta(
    val id: Int,
    val productoId: Int,
    val productoNombre: String,
    val cantidad: Int,
    val precioUnitario: String,
    val subtotal: String
)

data class CartItem(
    val productoId: Int,
    val nombre: String,
    val precio: String,
    val imagenUrl: String?,
    var cantidad: Int
)
