package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.model.Categoria
import com.diazmoviles.app.domain.model.Marca
import com.diazmoviles.app.domain.model.Producto
import com.diazmoviles.app.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductosUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val search: String = "",
    val error: String? = null,
    val hasMore: Boolean = true,
    val totalCount: Int = 0,
    val categoriaId: Int? = null,
    val marcaId: Int? = null,
    val marcas: List<Marca> = emptyList(),
    val categorias: List<Categoria> = emptyList()
)

@HiltViewModel
class ProductosViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductosUiState())
    val uiState: StateFlow<ProductosUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null
    private var currentCategoriaId: Int? = null
    private var currentMarcaId: Int? = null
    private var searchJob: Job? = null

    init {
        cargarMarcasYCategorias()
    }

    private fun cargarMarcasYCategorias() {
        viewModelScope.launch {
            productoRepository.listarMarcas().onSuccess {
                _uiState.value = _uiState.value.copy(marcas = it)
            }
            productoRepository.listarCategorias().onSuccess {
                _uiState.value = _uiState.value.copy(categorias = it)
            }
        }
    }

    private fun aplicarFiltros() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            currentPage = 1
            _uiState.value = _uiState.value.copy(
                isLoading = true, error = null, productos = emptyList()
            )
            val result = productoRepository.listarProductos(
                search = currentSearch,
                categoria = currentCategoriaId,
                marca = currentMarcaId,
                page = 1
            )
            result.fold(
                onSuccess = { pageResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        productos = pageResult.productos,
                        hasMore = pageResult.nextPage != null,
                        totalCount = pageResult.totalCount,
                        search = currentSearch ?: ""
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar"
                    )
                }
            )
        }
    }

    fun setCategoriaFilter(id: Int?) {
        currentCategoriaId = id
        currentMarcaId = null
        currentSearch = null
        _uiState.value = _uiState.value.copy(
            categoriaId = id, marcaId = null, search = ""
        )
        aplicarFiltros()
    }

    fun setMarcaFilter(id: Int?) {
        currentMarcaId = id
        aplicarFiltros()
    }

    fun cargarMas() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        currentPage++
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            val result = productoRepository.listarProductos(
                search = currentSearch,
                categoria = currentCategoriaId,
                marca = currentMarcaId,
                page = currentPage
            )
            result.fold(
                onSuccess = { pageResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        productos = _uiState.value.productos + pageResult.productos,
                        hasMore = pageResult.nextPage != null,
                        totalCount = pageResult.totalCount
                    )
                },
                onFailure = { e ->
                    currentPage--
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        error = e.message ?: "Error al cargar más"
                    )
                }
            )
        }
    }

    fun buscar(query: String) {
        currentSearch = query.ifBlank { null }
        _uiState.value = _uiState.value.copy(search = query)
        aplicarFiltros()
    }
}
