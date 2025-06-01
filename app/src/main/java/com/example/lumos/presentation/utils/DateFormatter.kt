package com.example.lumos.presentation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormatLong = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
    private val outputFormatShort = SimpleDateFormat("dd MMM", Locale("ru"))

    fun formatLong(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            outputFormatLong.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatShort(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            outputFormatShort.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}