package com.example.marchify.ui.livreur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marchify.data.repository.MissionRepository
import com.example.marchify.utils.PrefsManager

class MapViewModelFactory(
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            val missionRepository = MissionRepository(prefsManager)
            return MapViewModel(
                missionRepository = missionRepository,
                prefsManager = prefsManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
