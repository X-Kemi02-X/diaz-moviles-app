package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.model.Categoria
import com.diazmoviles.app.domain.model.Marca
import com.diazmoviles.app.domain.model.Producto
import com.diazmoviles.app.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProductUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val success: Boolean = false,
    val producto: Producto? = null,
    val marcas: List<Marca> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val error: String? = null,
    val isEditing: Boolean = false
)

@HiltViewModel
class AdminProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val productoId: Int? = savedStateHandle.get<Int>("productoId")

    private val _uiState = MutableStateFlow(AdminProductUiState())
    val uiState: StateFlow<AdminProductUiState> = _uiState.asStateFlow()

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            val esEdicion = productoId != null && productoId > 0
            _uiState.value = AdminProductUiState(isLoading = true, isEditing = esEdicion)

            val marcasResult = productoRepository.listarMarcas()
            val categoriasResult = productoRepository.listarCategorias()

            val marcas = marcasResult.getOrDefault(emptyList())
            val categorias = categoriasResult.getOrDefault(emptyList())

            if (esEdicion) {
                val productoResult = productoRepository.obtenerProducto(productoId!!)
                productoResult.fold(
                    onSuccess = { producto ->
                        _uiState.value = AdminProductUiState(
                            isLoading = false,
                            producto = producto,
                            marcas = marcas,
                            categorias = categorias,
                            isEditing = true
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = AdminProductUiState(
                            error = "Error al cargar: ${e.message}",
                            marcas = marcas,
                            categorias = categorias
                        )
                    }
                )
            } else {
                _uiState.value = AdminProductUiState(
                    isLoading = false,
                    marcas = marcas,
                    categorias = categorias
                )
            }
        }
    }

    fun guardarProducto(
        nombre: String, marcaId: Int, categoriaId: Int, modelo: String,
        precio: String, stock: Int, descripcion: String, imagenUrl: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val result = if (productoId != null) {
                productoRepository.actualizarProducto(
                    productoId, nombre, marcaId, categoriaId, modelo, precio, stock, descripcion, imagenUrl
                )
            } else {
                productoRepository.crearProducto(
                    nombre, marcaId, categoriaId, modelo, precio, stock, descripcion, imagenUrl
                )
            }
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSaving = false, success = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = e.message ?: "Error al guardar"
                    )
                }
            )
        }
    }
}
