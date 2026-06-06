package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.data.remote.api.CategoriaApi
import com.diazmoviles.app.data.remote.api.CreateCategoriaRequest
import com.diazmoviles.app.data.remote.api.CreateMarcaRequest
import com.diazmoviles.app.data.remote.api.MarcaApi
import com.diazmoviles.app.data.remote.dto.CategoriaDto
import com.diazmoviles.app.data.remote.dto.MarcaDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CatalogType { MARCA, CATEGORIA }

data class AdminCatalogUiState(
    val type: CatalogType = CatalogType.MARCA,
    val isLoading: Boolean = false,
    val items: List<Any> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AdminCatalogViewModel @Inject constructor(
    private val marcaApi: MarcaApi,
    private val categoriaApi: CategoriaApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCatalogUiState())
    val uiState: StateFlow<AdminCatalogUiState> = _uiState.asStateFlow()

    fun load(type: CatalogType) {
        _uiState.value = AdminCatalogUiState(type = type, isLoading = true)
        viewModelScope.launch {
            try {
                val items: List<Any> = if (type == CatalogType.MARCA) {
                    marcaApi.listarMarcas().body() ?: emptyList()
                } else {
                    categoriaApi.listarCategorias().body() ?: emptyList()
                }
                _uiState.value = AdminCatalogUiState(type = type, items = items)
            } catch (e: Exception) {
                _uiState.value = AdminCatalogUiState(type = type, error = e.message)
            }
        }
    }

    fun save(type: CatalogType, nombre: String, descripcion: String, editingId: Int? = null) {
        viewModelScope.launch {
            try {
                val success = if (editingId != null) {
                    if (type == CatalogType.MARCA) {
                        marcaApi.actualizarMarca(editingId, CreateMarcaRequest(nombre, descripcion)).isSuccessful
                    } else {
                        categoriaApi.actualizarCategoria(editingId, CreateCategoriaRequest(nombre, descripcion)).isSuccessful
                    }
                } else {
                    if (type == CatalogType.MARCA) {
                        marcaApi.crearMarca(CreateMarcaRequest(nombre, descripcion)).isSuccessful
                    } else {
                        categoriaApi.crearCategoria(CreateCategoriaRequest(nombre, descripcion)).isSuccessful
                    }
                }
                if (success) load(type)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun delete(type: CatalogType, item: Any) {
        viewModelScope.launch {
            val id = if (item is MarcaDto) item.id else (item as CategoriaDto).id
            try {
                if (type == CatalogType.MARCA) {
                    marcaApi.eliminarMarca(id)
                } else {
                    categoriaApi.eliminarCategoria(id)
                }
                load(type)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
