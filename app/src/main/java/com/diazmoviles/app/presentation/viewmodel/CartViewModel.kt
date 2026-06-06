package com.diazmoviles.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diazmoviles.app.domain.model.CartItem
import com.diazmoviles.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.items.collect { items ->
                val total = items.sumOf {
                    (it.precio.toDoubleOrNull() ?: 0.0) * it.cantidad
                }
                _uiState.value = CartUiState(items = items, total = total)
            }
        }
    }

    fun addItem(item: CartItem) {
        cartRepository.addItem(item)
    }

    fun removeItem(productoId: Int) {
        cartRepository.removeItem(productoId)
    }

    fun updateQuantity(productoId: Int, cantidad: Int) {
        cartRepository.updateQuantity(productoId, cantidad)
    }

    fun clear() {
        cartRepository.clear()
    }
}
