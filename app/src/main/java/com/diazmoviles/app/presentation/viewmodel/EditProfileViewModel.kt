package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.data.remote.api.AuthApi
import com.diazmoviles.app.data.remote.api.ChangePasswordRequest
import com.diazmoviles.app.domain.model.Cliente
import com.diazmoviles.app.domain.repository.AuthRepository
import com.diazmoviles.app.domain.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val saving: Boolean = false,
    val cliente: Cliente? = null,
    val error: String? = null,
    val success: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val clienteRepository: ClienteRepository,
    private val authRepository: AuthRepository,
    private val authApi: AuthApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init { cargarDatos() }

    private fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = EditProfileUiState(isLoading = true)
            val email = authRepository.getLoggedUser()?.email ?: return@launch
            val clientes = clienteRepository.listarClientes(search = email).getOrDefault(emptyList())
            val cliente = clientes.firstOrNull { it.email.equals(email, ignoreCase = true) }
            _uiState.value = EditProfileUiState(isLoading = false, cliente = cliente)
        }
    }

    fun guardarTodo(
        id: Int, nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String,
        oldPassword: String, newPassword: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saving = true, error = null, success = null)

            clienteRepository.actualizarCliente(id, nombre, apellido, cedula, email, telefono, direccion)
                .fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(saving = false, success = "Datos actualizados")
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(saving = false, error = e.message ?: "Error al guardar")
                        return@launch
                    }
                )

            if (oldPassword.isNotBlank() && newPassword.isNotBlank()) {
                _uiState.value = _uiState.value.copy(saving = true)
                try {
                    val response = authApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
                    if (response.isSuccessful) {
                        _uiState.value = _uiState.value.copy(saving = false, success = "Datos y contraseña actualizados")
                    } else {
                        val msg = response.errorBody()?.string() ?: response.code().toString()
                        _uiState.value = _uiState.value.copy(saving = false, error = msg)
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(saving = false, error = e.message ?: "Error")
                }
            }
        }
    }

    fun guardarCliente(
        id: Int, nombre: String, apellido: String, cedula: String,
        email: String, telefono: String, direccion: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saving = true, error = null, success = null)
            clienteRepository.actualizarCliente(id, nombre, apellido, cedula, email, telefono, direccion)
                .fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(saving = false, success = "Datos actualizados")
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(saving = false, error = e.message ?: "Error al guardar")
                    }
                )
        }
    }

    fun cambiarPassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saving = true, error = null, success = null)
            try {
                val response = authApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(saving = false, success = "Contraseña actualizada")
                } else {
                    val msg = response.errorBody()?.string() ?: response.code().toString()
                    _uiState.value = _uiState.value.copy(saving = false, error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(saving = false, error = e.message ?: "Error")
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, success = null)
    }
}
