package com.diazmoviles.app.data.repository

import com.diazmoviles.app.data.local.TokenDataStore
import com.diazmoviles.app.domain.model.CartItem
import com.diazmoviles.app.domain.repository.CartRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : CartRepository {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    override val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    override val total: StateFlow<Double> = _total.asStateFlow()

    private var currentUserId: Int? = null
    private val gson = Gson()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override suspend fun loadForUser(userId: Int) {
        currentUserId = userId
        val json = tokenDataStore.loadCartItems(userId)
        val list: List<CartItem> = if (json != null) {
            try {
                gson.fromJson(json, object : TypeToken<List<CartItem>>() {}.type) ?: emptyList()
            } catch (_: Exception) { emptyList() }
        } else emptyList()
        _items.value = list
        _total.value = list.sumOf { (it.precio.toDoubleOrNull() ?: 0.0) * it.cantidad }
    }

    override suspend fun persist() {
        val userId = currentUserId ?: return
        val json = gson.toJson(_items.value)
        tokenDataStore.saveCartItems(userId, json)
    }

    private fun autoSave() {
        ioScope.launch { persist() }
    }

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
        autoSave()
    }

    override fun removeItem(productoId: Int) {
        _items.value = _items.value.filter { it.productoId != productoId }
        recalculateTotal()
        autoSave()
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
        autoSave()
    }

    override fun clear() {
        _items.value = emptyList()
        recalculateTotal()
        autoSave()
    }

    private fun recalculateTotal() {
        _total.value = _items.value.sumOf {
            (it.precio.toDoubleOrNull() ?: 0.0) * it.cantidad
        }
    }
}
