package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClienteDto(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val cedula: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val activo: Boolean,
    @SerializedName("fecha_registro") val fechaRegistro: String,
    @SerializedName("fecha_actualizacion") val fechaActualizacion: String
)
