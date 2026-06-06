package com.diazmoviles.app.domain.model

data class Cliente(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val cedula: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val activo: Boolean
)
