package com.example.marchify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.marchify.navigation.MarchifyNavGraph
import com.example.marchify.utils.PrefsManager

class MainActivity : ComponentActivity() {

    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsManager = PrefsManager(applicationContext)

        setContent {
            MaterialTheme {  // ✅ Default Material3 theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    MarchifyNavGraph(
                        navController = navController,
                        prefsManager = prefsManager
                    )
                }
            }
        }
    }
}
