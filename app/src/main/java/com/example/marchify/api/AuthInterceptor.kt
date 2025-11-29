package com.example.marchify.api

import com.example.marchify.utils.PrefsManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp Interceptor that automatically adds JWT token to all API requests
 * Handles 401 Unauthorized by clearing expired tokens
 */
class AuthInterceptor(private val prefsManager: PrefsManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get stored JWT token
        val token = prefsManager.getAuthToken()

        // If no token exists, proceed without Authorization header
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Build new request with Authorization header
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        // Execute request
        val response = chain.proceed(authenticatedRequest)

        // Handle 401 Unauthorized - token expired or invalid
        if (response.code == 401) {
            response.close()

            // Clear expired token and user data
            prefsManager.clearAuth()

            // Note: You should trigger logout navigation in your ViewModel/Activity
            // This interceptor only clears the stored credentials
        }

        return response
    }
}
