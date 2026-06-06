package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.remote.api.CategoriaApi
import com.diazmoviles.app.data.remote.api.MarcaApi
import com.diazmoviles.app.data.remote.api.ProductoApi
import com.diazmoviles.app.data.remote.dto.CategoriaDto
import com.diazmoviles.app.data.remote.dto.CreateProductoRequest
import com.diazmoviles.app.data.remote.dto.MarcaDto
import com.diazmoviles.app.data.remote.dto.ProductoDto
import com.diazmoviles.app.domain.model.Categoria
import com.diazmoviles.app.domain.model.Marca
import com.diazmoviles.app.domain.model.Producto
import com.diazmoviles.app.domain.repository.ProductoRepository
import com.diazmoviles.app.domain.repository.ProductosPageResult
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoApi: ProductoApi,
    private val marcaApi: MarcaApi,
    private val categoriaApi: CategoriaApi
) : ProductoRepository {

    override suspend fun listarProductos(search: String?, page: Int?): Result<ProductosPageResult> {
        return runCatching {
            val response = productoApi.listarProductos(search = search, page = page)
            if (response.isSuccessful) {
                val body = response.body()!!
                val nextPage = body.next?.let { url ->
                    val pageParam = url.split("page=").getOrNull(1)?.split("&")?.firstOrNull()
                    pageParam?.toIntOrNull()
                }
                ProductosPageResult(
                    productos = body.results.map { it.toDomain() },
                    totalCount = body.count,
                    nextPage = nextPage
                )
            } else {
                throw Exception("Error al cargar productos: ${response.code()}")
            }
        }
    }

    override suspend fun obtenerProducto(id: Int): Result<Producto> {
        return runCatching {
            val response = productoApi.obtenerProducto(id)
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al obtener producto: ${response.code()}")
            }
        }
    }

    override suspend fun crearProducto(
        nombre: String, marca: Int, categoria: Int, modelo: String,
        precio: String, stock: Int, descripcion: String, imagenUrl: String
    ): Result<Producto> {
        return runCatching {
            val response = productoApi.crearProducto(
                CreateProductoRequest(nombre, marca, categoria, modelo, precio, stock, descripcion, imagenUrl)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al crear producto: ${response.code()}")
            }
        }
    }

    override suspend fun actualizarProducto(
        id: Int, nombre: String, marca: Int, categoria: Int, modelo: String,
        precio: String, stock: Int, descripcion: String, imagenUrl: String
    ): Result<Producto> {
        return runCatching {
            val response = productoApi.actualizarProducto(
                id, CreateProductoRequest(nombre, marca, categoria, modelo, precio, stock, descripcion, imagenUrl)
            )
            if (response.isSuccessful) {
                response.body()!!.toDomain()
            } else {
                throw Exception("Error al actualizar producto: ${response.code()}")
            }
        }
    }

    override suspend fun listarMarcas(): Result<List<Marca>> {
        return runCatching {
            val response = marcaApi.listarMarcas()
            if (response.isSuccessful) {
                response.body()!!.map { it.toDomain() }
            } else {
                throw Exception("Error al cargar marcas: ${response.code()}")
            }
        }
    }

    override suspend fun listarCategorias(): Result<List<Categoria>> {
        return runCatching {
            val response = categoriaApi.listarCategorias()
            if (response.isSuccessful) {
                response.body()!!.map { it.toDomain() }
            } else {
                throw Exception("Error al cargar categorías: ${response.code()}")
            }
        }
    }

    override suspend fun eliminarProducto(id: Int): Result<Unit> {
        return runCatching {
            val response = productoApi.eliminarProducto(id)
            if (!response.isSuccessful) {
                throw Exception("Error al eliminar producto: ${response.code()}")
            }
        }
    }

    private fun ProductoDto.toDomain() = Producto(
        id = id, nombre = nombre, marcaId = marca, marcaNombre = marcaNombre,
        categoriaId = categoria, categoriaNombre = categoriaNombre,
        modelo = modelo, precio = precio, stock = stock,
        descripcion = descripcion, imagenUrl = imagenUrl, activo = activo
    )

    private fun MarcaDto.toDomain() = Marca(
        id = id, nombre = nombre, descripcion = descripcion, activo = activo
    )

    private fun CategoriaDto.toDomain() = Categoria(
        id = id, nombre = nombre, descripcion = descripcion, activo = activo
    )
}
