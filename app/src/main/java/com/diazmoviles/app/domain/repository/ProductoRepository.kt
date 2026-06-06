package com.diazmoviles.app.domain.repository

import com.diazmoviles.app.domain.model.Categoria
import com.diazmoviles.app.domain.model.Marca
import com.diazmoviles.app.domain.model.Producto

data class ProductosPageResult(
    val productos: List<Producto>,
    val totalCount: Int,
    val nextPage: Int?
)

interface ProductoRepository {
    suspend fun listarProductos(search: String? = null, page: Int? = null): Result<ProductosPageResult>
    suspend fun obtenerProducto(id: Int): Result<Producto>
    suspend fun crearProducto(
        nombre: String, marca: Int, categoria: Int, modelo: String,
        precio: String, stock: Int, descripcion: String, imagenUrl: String
    ): Result<Producto>
    suspend fun actualizarProducto(
        id: Int, nombre: String, marca: Int, categoria: Int, modelo: String,
        precio: String, stock: Int, descripcion: String, imagenUrl: String
    ): Result<Producto>
    suspend fun listarMarcas(): Result<List<Marca>>
    suspend fun listarCategorias(): Result<List<Categoria>>
    suspend fun eliminarProducto(id: Int): Result<Unit>
}
