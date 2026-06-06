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

data class AdminUiState(
    val isLoading: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()
    private var currentSearch: String? = null

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = AdminUiState(isLoading = true)
            val result = productoRepository.listarProductos(search = currentSearch)
            result.fold(
                onSuccess = { pageResult ->
                    _uiState.value = AdminUiState(productos = pageResult.productos)
                },
                onFailure = { e ->
                    _uiState.value = AdminUiState(error = e.message ?: "Error al cargar")
                }
            )
        }
    }

    fun buscarProductos(query: String) {
        currentSearch = query.ifBlank { null }
        cargarProductos()
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            val result = productoRepository.eliminarProducto(id)
            result.fold(
                onSuccess = { cargarProductos() },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
            )
        }
    }
}
