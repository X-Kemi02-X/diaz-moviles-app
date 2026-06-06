package com.diazmoviles.app.domain.model

data class Marca(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean
)

data class Categoria(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean
)

data class Proveedor(
    val id: Int,
    val nombre: String,
    val contacto: String,
    val telefono: String,
    val email: String,
    val direccion: String,
    val activo: Boolean
)
