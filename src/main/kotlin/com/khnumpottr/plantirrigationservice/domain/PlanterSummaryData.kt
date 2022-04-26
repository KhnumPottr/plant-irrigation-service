package com.khnumpottr.plantirrigationservice.domain

import java.time.LocalDateTime

data class PlanterSummaryData(
    val planterId: String,
    val title: String? = null,
    val moistureLevel: Int? = null,
    val irrigating: Boolean = false,
    val dateReceived: LocalDateTime = LocalDateTime.now()
)
