package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MarcaDto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean
)

data class CategoriaDto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean
)

data class CreateProductoRequest(
    val nombre: String,
    val marca: Int,
    val categoria: Int,
    val modelo: String,
    val precio: String,
    val stock: Int,
    val descripcion: String = "",
    @SerializedName("imagen_url") val imagenUrl: String = "",
    val activo: Boolean = true
)
