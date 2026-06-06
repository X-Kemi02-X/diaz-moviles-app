package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.repository.CartRepository
import com.diazmoviles.app.domain.repository.ClienteRepository
import com.diazmoviles.app.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val ventaId: Int? = null,
    val error: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val ventaRepository: VentaRepository,
    private val clienteRepository: ClienteRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun realizarCheckout(
        nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String,
        metodoPago: String, observacion: String
    ) {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState(isLoading = true)

            val result = runCatching {
                val clienteResult = clienteRepository.crearCliente(
                    nombre, apellido, cedula, email, telefono, direccion
                )
                val cliente = clienteResult.getOrElse { throw Exception("Error al registrar cliente: ${it.message}") }

                val items = cartRepository.items.first()

                val venta = ventaRepository.crearVenta(
                    clienteId = cliente.id,
                    metodoPago = metodoPago,
                    observacion = observacion
                ).getOrElse { throw Exception("Error al crear venta: ${it.message}") }

                for (item in items) {
                    ventaRepository.crearDetalle(
                        ventaId = venta.id,
                        productoId = item.productoId,
                        cantidad = item.cantidad,
                        precioUnitario = item.precio
                    ).getOrElse { throw Exception("Error al crear detalle: ${it.message}") }
                }

                cartRepository.clear()
                venta
            }

            result.fold(
                onSuccess = { venta ->
                    _uiState.value = CheckoutUiState(success = true, ventaId = venta.id)
                },
                onFailure = { e ->
                    _uiState.value = CheckoutUiState(error = e.message ?: "Error en checkout")
                }
            )
        }
    }
}
