package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.model.Producto
import com.diazmoviles.app.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val totalCount: Int = 0
)

@HiltViewModel
class ProductosViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductosUiState())
    val uiState: StateFlow<ProductosUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null

    init {
        cargarProductos()
    }

    fun cargarProductos(search: String? = null) {
        currentPage = 1
        currentSearch = search
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, productos = emptyList())
            val result = productoRepository.listarProductos(search = search, page = 1)
            result.fold(
                onSuccess = { pageResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        productos = pageResult.productos,
                        hasMore = pageResult.nextPage != null,
                        totalCount = pageResult.totalCount,
                        search = search ?: ""
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

    fun cargarMas() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        currentPage++
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            val result = productoRepository.listarProductos(search = currentSearch, page = currentPage)
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
        cargarProductos(search = query.ifBlank { null })
    }
}
