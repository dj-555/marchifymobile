package com.example.marchify.api

import com.example.marchify.utils.Constants
import com.example.marchify.utils.PrefsManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object for Retrofit client configuration
 * Provides configured ApiService instance for making API calls
 */
object RetrofitClient {

    @Volatile
    private var retrofit: Retrofit? = null

    /**
     * Get or create Retrofit instance
     * Thread-safe singleton pattern
     */
    fun getInstance(prefsManager: PrefsManager): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit(prefsManager).also { retrofit = it }
        }
    }

    /**
     * Build Retrofit instance with OkHttp client
     */
    private fun buildRetrofit(prefsManager: PrefsManager): Retrofit {
        // Configure Gson for JSON parsing
        val gson = GsonBuilder()
            .setLenient()
            .create()

        // Build OkHttp client with interceptors
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefsManager))  // JWT token injection
            .addInterceptor(createLoggingInterceptor())     // Request/response logging
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        // Build Retrofit instance
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Create HTTP logging interceptor for debugging
     * Logs all requests and responses in debug builds
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            // In production, use: level = HttpLoggingInterceptor.Level.NONE
        }
    }

    /**
     * Get ApiService instance
     * Use this in your repositories
     */
    fun getApiService(prefsManager: PrefsManager): ApiService {
        return getInstance(prefsManager).create(ApiService::class.java)
    }

    /**
     * Clear cached Retrofit instance
     * Call this when changing BASE_URL or after logout
     */
    fun clearInstance() {
        retrofit = null
    }
}
