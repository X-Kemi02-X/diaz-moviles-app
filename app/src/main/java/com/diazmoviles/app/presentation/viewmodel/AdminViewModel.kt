package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.data.remote.api.CategoriaApi
import com.diazmoviles.app.data.remote.api.CreateCategoriaRequest
import com.diazmoviles.app.data.remote.api.CreateMarcaRequest
import com.diazmoviles.app.data.remote.api.MarcaApi
import com.diazmoviles.app.data.remote.dto.CategoriaDto
import com.diazmoviles.app.data.remote.dto.MarcaDto
import com.diazmoviles.app.domain.model.Cliente
import com.diazmoviles.app.domain.model.Producto
import com.diazmoviles.app.domain.repository.ClienteRepository
import com.diazmoviles.app.domain.repository.ProductoRepository
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
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val marcaApi: MarcaApi,
    private val categoriaApi: CategoriaApi,
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init { cargarTodo() }

    fun selectTab(index: Int) { _uiState.value = _uiState.value.copy(selectedTab = index) }

    fun cargarTodo() {
        cargarProductos()
        cargarMarcas()
        cargarCategorias()
        cargarClientes()
    }

    // ── Productos ──
    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingProductos = true)
            val s = _uiState.value.searchProductos.ifBlank { null }
            val result = productoRepository.listarProductos(search = s)
            result.fold(
                onSuccess = { pr -> _uiState.value = _uiState.value.copy(productos = pr.productos, isLoadingProductos = false) },
                onFailure = { e -> _uiState.value = _uiState.value.copy(error = e.message, isLoadingProductos = false) }
            )
        }
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
                val body = marcaApi.listarMarcas().body()
                val items = body?.results ?: emptyList()
                _uiState.value = _uiState.value.copy(marcas = items, isLoadingMarcas = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoadingMarcas = false)
            }
        }
    }

    fun guardarMarca(nombre: String, descripcion: String, editingId: Int? = null) {
        viewModelScope.launch {
            try {
                if (editingId != null) marcaApi.actualizarMarca(editingId, CreateMarcaRequest(nombre, descripcion))
                else marcaApi.crearMarca(CreateMarcaRequest(nombre, descripcion))
                cargarMarcas()
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun eliminarMarca(id: Int) {
        viewModelScope.launch {
            try { marcaApi.eliminarMarca(id); cargarMarcas() }
            catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun buscarMarcas(q: String) { _uiState.value = _uiState.value.copy(searchMarcas = q) }

    // ── Categorías ──
    fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCategorias = true)
            try {
                val body = categoriaApi.listarCategorias().body()
                val items = body?.results ?: emptyList()
                _uiState.value = _uiState.value.copy(categorias = items, isLoadingCategorias = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoadingCategorias = false)
            }
        }
    }

    fun guardarCategoria(nombre: String, descripcion: String, editingId: Int? = null) {
        viewModelScope.launch {
            try {
                if (editingId != null) categoriaApi.actualizarCategoria(editingId, CreateCategoriaRequest(nombre, descripcion))
                else categoriaApi.crearCategoria(CreateCategoriaRequest(nombre, descripcion))
                cargarCategorias()
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun eliminarCategoria(id: Int) {
        viewModelScope.launch {
            try { categoriaApi.eliminarCategoria(id); cargarCategorias() }
            catch (e: Exception) { _uiState.value = _uiState.value.copy(error = e.message) }
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

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
