package com.example.marchify.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.marchify.api.models.Adresse
import com.google.gson.Gson

class PrefsManager(context: Context) {

    private val gson = Gson()
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "marchify_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_TELEPHONE = "user_telephone"
        private const val KEY_USER_ADRESSE = "user_adresse"
        private const val KEY_VENDEUR_ID = "vendeur_id"
        private const val KEY_LIVREUR_ID = "livreur_id"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun saveUserData(
        id: String,
        role: String,
        name: String,
        email: String,
        telephone: String? = null,
        adresse: Adresse? = null,
        vendeurId: String? = null,
        livreurId: String? = null
    ) {
        prefs.edit().apply {
            putString(KEY_USER_ID, id)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            telephone?.let { putString(KEY_USER_TELEPHONE, it) }
            adresse?.let {
                val json = gson.toJson(it)
                putString(KEY_USER_ADRESSE, json)
            }
            vendeurId?.let { putString(KEY_VENDEUR_ID, it) }
            livreurId?.let { putString(KEY_LIVREUR_ID, it) }
            apply()
        }
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserTelephone(): String? = prefs.getString(KEY_USER_TELEPHONE, null)

    fun getUserAdresse(): Adresse? {
        val json = prefs.getString(KEY_USER_ADRESSE, null)
        return if (json != null) {
            gson.fromJson(json, Adresse::class.java)
        } else {
            null
        }
    }

    fun getVendeurId(): String? = prefs.getString(KEY_VENDEUR_ID, null)
    fun getLivreurId(): String? = prefs.getString(KEY_LIVREUR_ID, null)

    fun isLoggedIn(): Boolean = getAuthToken() != null

    fun clearAuth() {
        prefs.edit().clear().apply()
    }
}
