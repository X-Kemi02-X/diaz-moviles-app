package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProveedorDto(
    val id: Int,
    val nombre: String,
    val contacto: String,
    val telefono: String,
    val email: String,
    val direccion: String,
    val activo: Boolean,
    @SerializedName("fecha_creacion") val fechaCreacion: String?
)
