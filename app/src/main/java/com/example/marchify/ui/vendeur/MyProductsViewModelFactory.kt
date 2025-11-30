package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.data.repository.ProductRepository
import com.example.marchify.utils.PrefsManager

/**
 * Factory for MyProductsViewModel
 */
class MyProductsViewModelFactory(
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyProductsViewModel::class.java)) {
            val boutiqueRepository = BoutiqueRepository(prefsManager)
            val productRepository = ProductRepository(prefsManager)
            return MyProductsViewModel(
                boutiqueRepository = boutiqueRepository,
                productRepository = productRepository,
                prefsManager = prefsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
