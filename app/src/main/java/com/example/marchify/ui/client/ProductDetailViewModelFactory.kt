package com.example.marchify.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marchify.data.repository.CartRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.utils.PrefsManager

class ProductDetailViewModelFactory(
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            val productRepository = ProductRepository(prefsManager)
            val cartRepository = CartRepository(prefsManager)

            return ProductDetailViewModel(
                productRepository = productRepository,
                cartRepository = cartRepository,
                prefsManager = prefsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
