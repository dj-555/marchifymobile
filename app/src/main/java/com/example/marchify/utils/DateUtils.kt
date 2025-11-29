package com.example.marchify.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Utility functions for date formatting and manipulation
 */
object DateUtils {

    private const val DATE_FORMAT_FULL = "dd/MM/yyyy HH:mm"
    private const val DATE_FORMAT_SHORT = "dd/MM/yyyy"
    private const val DATE_FORMAT_TIME = "HH:mm"
    private const val DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DATE_FORMAT_READABLE = "dd MMMM yyyy à HH:mm" // New format

    private val locale = Locale.FRENCH

    /**
     * Format date to full format: 28/11/2025 14:30
     */
    fun formatFull(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_FULL, locale).format(date)
    }

    /**
     * Format date to short format: 28/11/2025
     */
    fun formatShort(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_SHORT, locale).format(date)
    }

    /**
     * Format date to time only: 14:30
     */
    fun formatTime(date: Date): String {
        return SimpleDateFormat(DATE_FORMAT_TIME, locale).format(date)
    }

    /**
     * Parse ISO date string from API
     * Supports multiple ISO formats
     */
    fun parseIso(dateString: String): Date? {
        return try {
            // Try with milliseconds
            SimpleDateFormat(DATE_FORMAT_ISO, Locale.US).parse(dateString)
        } catch (e: Exception) {
            try {
                // Try without milliseconds
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(dateString)
            } catch (e2: Exception) {
                try {
                    // Try simple format
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(dateString)
                } catch (e3: Exception) {
                    null
                }
            }
        }
    }

    /**
     * Format ISO string to full format: 28/11/2025 14:30
     */
    fun formatIsoToFull(isoString: String): String {
        val date = parseIso(isoString)
        return if (date != null) formatFull(date) else isoString
    }

    /**
     * Format ISO string to readable format: 28 novembre 2025 à 14:30
     * This is the function you need!
     */
    fun formatIsoToReadable(isoString: String): String {
        val date = parseIso(isoString)
        return if (date != null) {
            SimpleDateFormat(DATE_FORMAT_READABLE, locale).format(date)
        } else {
            isoString
        }
    }

    /**
     * Format ISO string to short readable: 28 novembre 2025
     */
    fun formatIsoToReadableShort(isoString: String): String {
        val date = parseIso(isoString)
        return if (date != null) {
            SimpleDateFormat("dd MMMM yyyy", locale).format(date)
        } else {
            isoString
        }
    }

    /**
     * Get relative time string (il y a 5 minutes, il y a 2 heures, etc.)
     */
    fun getRelativeTime(date: Date): String {
        val now = Date()
        val diff = now.time - date.time

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "À l'instant"
            minutes < 60 -> "Il y a $minutes min"
            hours < 24 -> "Il y a $hours h"
            days < 7 -> "Il y a $days j"
            else -> formatShort(date)
        }
    }

    /**
     * Get relative time from ISO string
     */
    fun getRelativeTimeFromIso(isoString: String): String {
        val date = parseIso(isoString)
        return if (date != null) {
            getRelativeTime(date)
        } else {
            isoString
        }
    }

    /**
     * Check if date is today
     */
    fun isToday(date: Date): Boolean {
        val today = SimpleDateFormat(DATE_FORMAT_SHORT, locale).format(Date())
        val dateStr = SimpleDateFormat(DATE_FORMAT_SHORT, locale).format(date)
        return today == dateStr
    }

    /**
     * Check if ISO date is today
     */
    fun isIsoToday(isoString: String): Boolean {
        val date = parseIso(isoString)
        return if (date != null) isToday(date) else false
    }

    /**
     * Get current timestamp
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Format timestamp to readable format
     */
    fun formatTimestampToReadable(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat(DATE_FORMAT_READABLE, locale).format(date)
    }
}
