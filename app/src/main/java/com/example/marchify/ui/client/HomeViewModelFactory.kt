package com.example.marchify.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.CartRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.utils.PrefsManager

class HomeViewModelFactory(
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val boutiqueRepository = BoutiqueRepository(prefsManager)
            val productRepository = ProductRepository(prefsManager)
            val cartRepository = CartRepository(prefsManager)

            return HomeViewModel(
                boutiqueRepository = boutiqueRepository,
                productRepository = productRepository,
                cartRepository = cartRepository,
                prefsManager = prefsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
