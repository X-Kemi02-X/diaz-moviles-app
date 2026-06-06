package com.diazmoviles.app.domain.model

data class Producto(
    val id: Int,
    val nombre: String,
    val marcaId: Int,
    val marcaNombre: String,
    val categoriaId: Int,
    val categoriaNombre: String,
    val modelo: String,
    val precio: String,
    val stock: Int,
    val descripcion: String,
    val imagenUrl: String?,
    val activo: Boolean
)
