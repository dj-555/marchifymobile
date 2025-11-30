package com.example.marchify.ui.vendeur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marchify.data.repository.BoutiqueRepository
import com.example.marchify.utils.PrefsManager

/**
 * Factory for MyBoutiquesViewModel
 */
class MyBoutiquesViewModelFactory(
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyBoutiquesViewModel::class.java)) {
            val boutiqueRepository = BoutiqueRepository(prefsManager)
            return MyBoutiquesViewModel(
                boutiqueRepository = boutiqueRepository,
                prefsManager = prefsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
