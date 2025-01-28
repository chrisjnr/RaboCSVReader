package com.example.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.threeten.bp.LocalDateTime

class UtilsTest {

    @Test
    fun `given valid date formats when parseStringToLocalDateTime then returns expected LocalDateTime`() {
        val testData = mapOf(
            "2025-01-26T14:30:00" to "2025-01-26T14:30:00",
            "2025/01/26 14:30:00" to "2025-01-26T14:30:00",
            "26-01-2025 14:30" to "2025-01-26T14:30:00",
            "2025-01-26 14:30:00" to "2025-01-26T14:30:00",
            "01/26/2025" to "2025-01-26T00:00:00",
            "2025-01-26" to "2025-01-26T00:00:00"
        )

        testData.forEach { (input, expected) ->
            val result = parseStringToLocalDateTime(input)
            val expectedDateTime = LocalDateTime.parse(expected)
            assertNotNull(result)
            assertEquals(expectedDateTime, result)
        }
    }

    @Test
    fun `given invalid date formats when parseStringToLocalDateTime then returns null`() {
        val invalidDates = listOf(
            "2025-01-26T14:30",
            "2025-13-26T14:30:00",
            "26-2025-01 14:30:00",
            "2025/26/01 14:30:00",
            "2025-01-26 ABC",
            "random string"
        )

        invalidDates.forEach {
            val result = parseStringToLocalDateTime(it)
            assertNull(result)
        }
    }

    @Test
    fun `given valid LocalDateTime when formatLocalDateToString then returns formatted date`() {
        val localDate = LocalDateTime.of(2025, 1, 26, 14, 30, 0, 0)
        val formattedDate = formatLocalDateToString(localDate)
        assertEquals("26 Jan 2025", formattedDate)
    }

    @Test
    fun `given null LocalDateTime when formatLocalDateToString then returns empty string`() {
        val formattedDate = formatLocalDateToString(null)
        assertEquals("", formattedDate)
    }

    @Test
    fun `given edge LocalDateTime when formatLocalDateToString then returns formatted date`() {
        val localDate = LocalDateTime.of(1999, 12, 31, 23, 59, 59, 999999999)
        val formattedDate = formatLocalDateToString(localDate)
        assertEquals("31 Dec 1999", formattedDate)
    }

    @Test
    fun `given URL with file extension when getFileName then returns file name`() {
        val url = "https://example.com/files/test.csv"
        val fileName = getFileName(url)
        assertEquals("test.csv", fileName)
    }

    @Test
    fun `given URL without file extension when getFileName then appends default extension`() {
        val url = "https://example.com/files/test"
        val fileName = getFileName(url)
        assertEquals("test.csv", fileName)
    }

    @Test
    fun `given URL with invalid extension when getFileName then returns default file name`() {
        val url = "https://example.com/files/test.pdf"
        val fileName = getFileName(url)
        assertEquals("test.csv", fileName)
    }

    @Test
    fun `given URL with empty last path when getFileName then returns default file name`() {
        val url = "https://example.com/files/"
        val fileName = getFileName(url)
        assertEquals("file_download.csv", fileName)
    }

    @Test
    fun `given invalid URL when getFileName then returns default file name`() {
        val url = "invalid_url"
        val fileName = getFileName(url)
        assertEquals("file_download.csv", fileName)
    }

    @Test
    fun `given URL with multiple path parts when getFileName then returns correct file name`() {
        val url = "https://example.com/download/file/test.csv"
        val fileName = getFileName(url)
        assertEquals("test.csv", fileName)
    }

    @Test
    fun `given URL with name without extension and ends with slash when getFileName then returns default file name`() {
        val url = "https://example.com/files/test/"
        val fileName = getFileName(url)
        assertEquals("file_download.csv", fileName)
    }

    @Test
    fun `given URL with no path when getFileName then returns default file name`() {
        val url = "https://example.com"
        val fileName = getFileName(url)
        assertEquals("file_download.csv", fileName)
    }

    @Test
    fun `given malformed URL when getFileName then returns default file name`() {
        val url = "htp://invalid-url"
        val fileName = getFileName(url)
        assertEquals("file_download.csv", fileName)
    }

    @Test
    fun `given URL with empty path when getFileName then returns default file name`() {
        val url = "https://example.com//"
        val fileName = getFileName(url)
        assertEquals("file_download.csv", fileName)
    }
}


