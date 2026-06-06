package com.diazmoviles.app.data.repository

import com.diazmoviles.app.domain.model.CartItem
import com.diazmoviles.app.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    override val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    override val total: StateFlow<Double> = _total.asStateFlow()

    override fun addItem(item: CartItem) {
        val current = _items.value.toMutableList()
        val existing = current.indexOfFirst { it.productoId == item.productoId }
        if (existing >= 0) {
            current[existing] = current[existing].copy(
                cantidad = current[existing].cantidad + item.cantidad
            )
        } else {
            current.add(item)
        }
        _items.value = current
        recalculateTotal()
    }

    override fun removeItem(productoId: Int) {
        _items.value = _items.value.filter { it.productoId != productoId }
        recalculateTotal()
    }

    override fun updateQuantity(productoId: Int, cantidad: Int) {
        if (cantidad <= 0) {
            removeItem(productoId)
            return
        }
        _items.value = _items.value.map {
            if (it.productoId == productoId) it.copy(cantidad = cantidad) else it
        }
        recalculateTotal()
    }

    override fun clear() {
        _items.value = emptyList()
        recalculateTotal()
    }

    private fun recalculateTotal() {
        _total.value = _items.value.sumOf {
            (it.precio.toDoubleOrNull() ?: 0.0) * it.cantidad
        }
    }
}
