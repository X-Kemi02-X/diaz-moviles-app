package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.model.Cliente
import com.diazmoviles.app.domain.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val cliente: Cliente? = null,
    val error: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registrar(
        nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            val result = clienteRepository.crearCliente(
                nombre, apellido, cedula, email, telefono, direccion
            )
            result.fold(
                onSuccess = { cliente ->
                    _uiState.value = RegisterUiState(success = true, cliente = cliente)
                },
                onFailure = { e ->
                    _uiState.value = RegisterUiState(error = e.message ?: "Error al registrar")
                }
            )
        }
    }
}
