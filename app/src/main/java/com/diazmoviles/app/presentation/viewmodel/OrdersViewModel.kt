package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.model.Venta
import com.diazmoviles.app.domain.repository.AuthRepository
import com.diazmoviles.app.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val isLoading: Boolean = false,
    val ventas: List<Venta> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val ventaRepository: VentaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        cargarVentas()
    }

    fun cargarVentas() {
        viewModelScope.launch {
            _uiState.value = OrdersUiState(isLoading = true)
            val userId = authRepository.getLoggedUser()?.userId
            val result = ventaRepository.listarVentas(usuarioId = userId)
            result.fold(
                onSuccess = { ventas ->
                    _uiState.value = OrdersUiState(ventas = ventas)
                },
                onFailure = { e ->
                    _uiState.value = OrdersUiState(error = e.message ?: "Error al cargar ventas")
                }
            )
        }
    }
}
