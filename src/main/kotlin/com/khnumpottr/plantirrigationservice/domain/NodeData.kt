package com.khnumpottr.plantirrigationservice.domain

import java.time.LocalDate

data class NodeData(
    val planterId: String,
    val title: String? = null,
    val datePlanted: LocalDate? = null,
    val upperLimit: Int? = null,
    val lowerLimit: Int? = null,
    val plants: String? = null
)
