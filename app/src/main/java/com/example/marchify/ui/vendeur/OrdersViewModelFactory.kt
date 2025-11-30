package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marchify.data.repository.OrderRepository
import com.example.marchify.utils.PrefsManager

/**
 * Factory for OrdersViewModel
 */
class OrdersViewModelFactory(
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            val orderRepository = OrderRepository(prefsManager)
            return OrdersViewModel(
                orderRepository = orderRepository,
                prefsManager = prefsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
