package com.example.core.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URL



fun parseStringToLocalDateTime(dateValue: String): LocalDateTime? {
    val commonFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy/MM/dd HH:mm:ss",
        "dd-MM-yyyy HH:mm",
        "yyyy-MM-dd H:mm:ss",
        "MM/dd/yyyy",
        "yyyy-MM-dd",
    )

    val detectedFormatter = commonFormats.find { format ->
        try {
            DateTimeFormatter.ofPattern(format).parse(dateValue)
            true
        } catch (e: Exception) {
            false
        }
    }

    return try {
        detectedFormatter?.let { format ->
            LocalDateTime.parse(dateValue, DateTimeFormatter.ofPattern(format))
        }
    } catch (e: Exception) {
        try {
            detectedFormatter?.let { format ->
                LocalDate.parse(dateValue, DateTimeFormatter.ofPattern(format))
            }?.atStartOfDay()
        } catch (e: Exception) {
            null
        }
    }
}

fun formatLocalDateToString(localDate: LocalDateTime?): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return localDate?.format(formatter)?:""
}

fun getFileName(urlString: String): String {
    val defaultName = "file_download.csv"
    return try {
        val lastPart = URL(urlString).path.substringAfterLast("/")
        when {
            lastPart.isEmpty() -> defaultName
            lastPart.contains(".") -> {
                val extension = lastPart.substringAfterLast(".")
                if (extension == "csv") lastPart else "${lastPart.substringBeforeLast(".")}.csv"
            }
            else -> "$lastPart.csv"
        }
    } catch (e: Exception) {
        defaultName
    }
}

