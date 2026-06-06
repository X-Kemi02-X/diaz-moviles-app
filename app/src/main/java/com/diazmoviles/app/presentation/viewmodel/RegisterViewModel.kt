package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.repository.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registrar(
        username: String, password: String, email: String,
        nombre: String, apellido: String, cedula: String,
        telefono: String, direccion: String
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            val result = registerRepository.register(
                username, password, email,
                nombre, apellido, cedula, telefono, direccion
            )
            result.fold(
                onSuccess = {
                    _uiState.value = RegisterUiState(success = true)
                },
                onFailure = { e ->
                    _uiState.value = RegisterUiState(error = e.message ?: "Error al registrarse")
                }
            )
        }
    }
}
