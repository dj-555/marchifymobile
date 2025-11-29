package com.example.marchify.utils

import android.util.Patterns

/**
 * Utility functions for form validation
 */
object ValidationUtils {

    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate password (min 6 characters)
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }

    /**
     * Validate phone number (min 8 digits)
     */
    fun isValidPhone(phone: String): Boolean {
        val digitsOnly = phone.replace(Regex("[^0-9]"), "")
        return digitsOnly.length >= Constants.MIN_PHONE_LENGTH
    }

    /**
     * Validate Tunisian phone number format
     */
    fun isValidTunisianPhone(phone: String): Boolean {
        val digitsOnly = phone.replace(Regex("[^0-9]"), "")
        // Tunisian numbers: 8 digits starting with 2, 3, 4, 5, 7, or 9
        return digitsOnly.length == 8 && digitsOnly[0] in listOf('2', '3', '4', '5', '7', '9')
    }

    /**
     * Validate name (not empty, only letters and spaces)
     */
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.matches(Regex("^[a-zA-ZÀ-ÿ\\s'-]+$"))
    }

    /**
     * Validate price (positive number)
     */
    fun isValidPrice(price: String): Boolean {
        return try {
            val priceDouble = price.toDouble()
            priceDouble > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Validate quantity (positive integer)
     */
    fun isValidQuantity(quantity: String): Boolean {
        return try {
            val quantityInt = quantity.toInt()
            quantityInt > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Validate postal code (4-digit Tunisian format)
     */
    fun isValidPostalCode(postalCode: String): Boolean {
        return postalCode.matches(Regex("^[0-9]{4}$"))
    }

    /**
     * Validate address (not empty)
     */
    fun isValidAddress(address: String): Boolean {
        return address.isNotBlank() && address.length >= 5
    }

    /**
     * Get email error message
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "L'email est requis"
            !isValidEmail(email) -> "Email invalide"
            else -> null
        }
    }

    /**
     * Get password error message
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "Le mot de passe est requis"
            !isValidPassword(password) -> "Au moins ${Constants.MIN_PASSWORD_LENGTH} caractères requis"
            else -> null
        }
    }

    /**
     * Get phone error message
     */
    fun getPhoneError(phone: String): String? {
        return when {
            phone.isBlank() -> "Le numéro de téléphone est requis"
            !isValidTunisianPhone(phone) -> "Numéro de téléphone invalide (8 chiffres)"
            else -> null
        }
    }

    /**
     * Get name error message
     */
    fun getNameError(name: String): String? {
        return when {
            name.isBlank() -> "Le nom est requis"
            !isValidName(name) -> "Le nom ne peut contenir que des lettres"
            else -> null
        }
    }
}
