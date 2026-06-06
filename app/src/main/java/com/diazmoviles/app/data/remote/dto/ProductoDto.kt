package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductoDto(
    val id: Int,
    val nombre: String,
    val marca: Int,
    val categoria: Int,
    val modelo: String,
    val precio: String,
    val stock: Int,
    val descripcion: String,
    @SerializedName("marca_nombre") val marcaNombre: String,
    @SerializedName("categoria_nombre") val categoriaNombre: String,
    @SerializedName("imagen_url") val imagenUrl: String?,
    val activo: Boolean,
    @SerializedName("fecha_creacion") val fechaCreacion: String
)
