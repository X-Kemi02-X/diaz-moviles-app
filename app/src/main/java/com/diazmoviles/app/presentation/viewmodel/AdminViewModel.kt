package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.data.remote.api.CategoriaApi
import com.diazmoviles.app.data.remote.api.CreateCategoriaRequest
import com.diazmoviles.app.data.remote.api.CreateMarcaRequest
import com.diazmoviles.app.data.remote.api.MarcaApi
import com.diazmoviles.app.data.remote.dto.CategoriaDto
import com.diazmoviles.app.data.remote.dto.MarcaDto
import com.diazmoviles.app.data.remote.util.parseError
import com.diazmoviles.app.data.remote.util.toUserMessage
import com.diazmoviles.app.domain.model.Cliente
import com.diazmoviles.app.domain.model.Producto
import com.diazmoviles.app.domain.model.Venta
import com.diazmoviles.app.domain.repository.ClienteRepository
import com.diazmoviles.app.domain.repository.ProductoRepository
import com.diazmoviles.app.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val selectedTab: Int = 0,
    val isLoadingProductos: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val currentPage: Int = 1,
    val hasMorePages: Boolean = false,
    val totalCount: Int = 0,
    val searchProductos: String = "",
    val isLoadingMarcas: Boolean = false,
    val marcas: List<MarcaDto> = emptyList(),
    val searchMarcas: String = "",
    val isLoadingCategorias: Boolean = false,
    val categorias: List<CategoriaDto> = emptyList(),
    val searchCategorias: String = "",
    val isLoadingClientes: Boolean = false,
    val clientes: List<Cliente> = emptyList(),
    val searchClientes: String = "",
    val isLoadingVentas: Boolean = false,
    val ventas: List<Venta> = emptyList(),
    val searchVentas: String = "",
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val marcaApi: MarcaApi,
    private val categoriaApi: CategoriaApi,
    private val clienteRepository: ClienteRepository,
    private val ventaRepository: VentaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private var currentSearchProductos: String? = null

    init { cargarTodo() }

    fun selectTab(index: Int) { _uiState.value = _uiState.value.copy(selectedTab = index) }

    fun cargarTodo() {
        cargarProductos()
        cargarMarcas()
        cargarCategorias()
        cargarClientes()
        cargarVentas()
    }

    // ── Productos ──
    fun cargarProductos() {
        val s = _uiState.value.searchProductos.ifBlank { null }
        currentSearchProductos = s
        cargarPagina(1)
    }

    private fun cargarPagina(page: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingProductos = true, currentPage = page
            )
            val result = productoRepository.listarProductos(
                search = currentSearchProductos, page = page
            )
            result.fold(
                onSuccess = { pr ->
                    _uiState.value = _uiState.value.copy(
                        productos = pr.productos, isLoadingProductos = false,
                        hasMorePages = pr.nextPage != null, totalCount = pr.totalCount
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingProductos = false, error = e.message
                    )
                }
            )
        }
    }

    fun irPaginaAnterior() {
        val page = _uiState.value.currentPage
        if (page > 1) cargarPagina(page - 1)
    }

    fun irPaginaSiguiente() {
        cargarPagina(_uiState.value.currentPage + 1)
    }

    fun buscarProductos(q: String) {
        _uiState.value = _uiState.value.copy(searchProductos = q)
        cargarProductos()
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            val result = productoRepository.eliminarProducto(id)
            result.fold(
                onSuccess = { cargarProductos() },
                onFailure = { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            )
        }
    }

    // ── Marcas ──
    fun cargarMarcas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMarcas = true)
            try {
                val response = marcaApi.listarMarcas()
                if (response.isSuccessful) {
                    val items = response.body()?.results ?: emptyList()
                    _uiState.value = _uiState.value.copy(marcas = items, isLoadingMarcas = false)
                } else {
                    _uiState.value = _uiState.value.copy(error = response.parseError(), isLoadingMarcas = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.toUserMessage(), isLoadingMarcas = false)
            }
        }
    }

    fun guardarMarca(nombre: String, descripcion: String, editingId: Int? = null) {
        viewModelScope.launch {
            try {
                if (editingId != null) {
                    val r = marcaApi.actualizarMarca(editingId, CreateMarcaRequest(nombre, descripcion))
                    if (!r.isSuccessful) throw Exception(r.parseError())
                } else {
                    val r = marcaApi.crearMarca(CreateMarcaRequest(nombre, descripcion))
                    if (!r.isSuccessful) throw Exception(r.parseError())
                }
                cargarMarcas()
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.toUserMessage()) }
        }
    }

    fun eliminarMarca(id: Int) {
        viewModelScope.launch {
            try {
                val r = marcaApi.eliminarMarca(id)
                if (!r.isSuccessful) throw Exception(r.parseError())
                cargarMarcas()
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.toUserMessage()) }
        }
    }

    fun buscarMarcas(q: String) { _uiState.value = _uiState.value.copy(searchMarcas = q) }

    // ── Categorías ──
    fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCategorias = true)
            try {
                val response = categoriaApi.listarCategorias()
                if (response.isSuccessful) {
                    val items = response.body()?.results ?: emptyList()
                    _uiState.value = _uiState.value.copy(categorias = items, isLoadingCategorias = false)
                } else {
                    _uiState.value = _uiState.value.copy(error = response.parseError(), isLoadingCategorias = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.toUserMessage(), isLoadingCategorias = false)
            }
        }
    }

    fun guardarCategoria(nombre: String, descripcion: String, editingId: Int? = null) {
        viewModelScope.launch {
            try {
                if (editingId != null) {
                    val r = categoriaApi.actualizarCategoria(editingId, CreateCategoriaRequest(nombre, descripcion))
                    if (!r.isSuccessful) throw Exception(r.parseError())
                } else {
                    val r = categoriaApi.crearCategoria(CreateCategoriaRequest(nombre, descripcion))
                    if (!r.isSuccessful) throw Exception(r.parseError())
                }
                cargarCategorias()
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.toUserMessage()) }
        }
    }

    fun eliminarCategoria(id: Int) {
        viewModelScope.launch {
            try {
                val r = categoriaApi.eliminarCategoria(id)
                if (!r.isSuccessful) throw Exception(r.parseError())
                cargarCategorias()
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.toUserMessage()) }
        }
    }

    fun buscarCategorias(q: String) { _uiState.value = _uiState.value.copy(searchCategorias = q) }

    // ── Clientes ──
    fun cargarClientes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingClientes = true)
            clienteRepository.listarClientes().fold(
                onSuccess = { items -> _uiState.value = _uiState.value.copy(clientes = items, isLoadingClientes = false) },
                onFailure = { e -> _uiState.value = _uiState.value.copy(error = e.message, isLoadingClientes = false) }
            )
        }
    }

    fun eliminarCliente(id: Int) {
        viewModelScope.launch {
            clienteRepository.eliminarCliente(id).fold(
                onSuccess = { cargarClientes() },
                onFailure = { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            )
        }
    }

    fun buscarClientes(q: String) { _uiState.value = _uiState.value.copy(searchClientes = q) }

    // ── Ventas ──
    fun cargarVentas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingVentas = true)
            ventaRepository.listarVentas().fold(
                onSuccess = { items -> _uiState.value = _uiState.value.copy(ventas = items, isLoadingVentas = false) },
                onFailure = { e -> _uiState.value = _uiState.value.copy(error = e.message, isLoadingVentas = false) }
            )
        }
    }

    fun actualizarEstadoVenta(id: Int, estado: String) {
        viewModelScope.launch {
            ventaRepository.actualizarEstadoVenta(id, estado).fold(
                onSuccess = { cargarVentas() },
                onFailure = { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            )
        }
    }

    fun eliminarVenta(id: Int) {
        viewModelScope.launch {
            ventaRepository.eliminarVenta(id).fold(
                onSuccess = { cargarVentas() },
                onFailure = { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            )
        }
    }

    fun buscarVentas(q: String) { _uiState.value = _uiState.value.copy(searchVentas = q) }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
