package com.khnumpottr.plantirrigationservice.domain

data class NodeSummaryData(
    val nodeName: String,
    val moistureLevel: Int? = null,
    val irrigating: Boolean = false,
)
