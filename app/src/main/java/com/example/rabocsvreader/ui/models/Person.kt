package com.example.rabocsvreader.ui.models

import org.threeten.bp.LocalDateTime


data class Person(val firstName: String, val surname: String, val issueCount: Int, val dob: LocalDateTime?, val avatar: String)
