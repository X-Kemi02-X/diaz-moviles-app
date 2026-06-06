package com.diazmoviles.app.domain.repository

import com.diazmoviles.app.domain.model.CartItem
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val items: StateFlow<List<CartItem>>
    val total: StateFlow<Double>

    fun addItem(item: CartItem)
    fun removeItem(productoId: Int)
    fun updateQuantity(productoId: Int, cantidad: Int)
    fun clear()
    suspend fun loadForUser(userId: Int)
    suspend fun persist()
}
