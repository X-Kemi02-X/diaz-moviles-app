package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
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

data class DetalleProductoUiState(
    val isLoading: Boolean = false,
    val producto: Producto? = null,
    val error: String? = null
)

@HiltViewModel
class DetalleProductoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val productoId: Int = savedStateHandle["productoId"] ?: 0

    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProducto()
    }

    fun cargarProducto() {
        viewModelScope.launch {
            _uiState.value = DetalleProductoUiState(isLoading = true)
            val result = productoRepository.obtenerProducto(productoId)
            result.fold(
                onSuccess = { producto ->
                    _uiState.value = DetalleProductoUiState(producto = producto)
                },
                onFailure = { e ->
                    _uiState.value = DetalleProductoUiState(
                        error = e.message ?: "Error al cargar producto"
                    )
                }
            )
        }
    }
}
