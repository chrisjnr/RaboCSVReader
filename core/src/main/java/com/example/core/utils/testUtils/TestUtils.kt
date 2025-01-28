package com.example.core.utils.testUtils

import java.io.File

fun generateErrorRow(): List<String> {
    return listOf(
        "\"Mona\",\"Lisa\",INVALID,\"1948-01-02T00:00:00\",\"https://api.multiavatar.com/2cdf5db9b4dee297b7.png\"",
        "\"Joshua\",\"Zirkzee\",,\"2007-04-20T00:00:00\",\"https://api.multiavatar.com/2672c49d6099f87274.png\""
    )
}

fun generateCSVFile(moreRows: List<String> = emptyList()): File {
    val file = File.createTempFile("testFile", ".csv")
    val validRows = listOf(
        "\"Theo\",\"Jansen\",5,\"1978-01-02T00:00:00\",\"https://api.multiavatar.com/2cdf5db9b4dee297b7.png\"",
        "\"Fiona\",\"de Vries\",7,\"1950-11-12T00:00:00\",\"https://api.multiavatar.com/b9339cb9e7a833cd5e.png\"",
        "\"Petra\",\"Boersma\",1,\"2001-04-20T00:00:00\",\"https://api.multiavatar.com/2672c49d6099f87274.png\""
    )
    val rows = mutableListOf<String>()
    rows.add("\"First name\",\"Sur name\",\"Issue count\",\"Date of birth\",\"avatar\"")
    rows.addAll(validRows)

    rows.addAll(moreRows)

    file.writeText(rows.joinToString("\n"))
    return file
}
