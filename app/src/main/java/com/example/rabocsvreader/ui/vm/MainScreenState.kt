package com.example.rabocsvreader.ui.vm

import com.example.rabocsvreader.ui.models.Person

data class MainScreenState(
    val peopleList: List<Person>,
    val errorCount: Int
)