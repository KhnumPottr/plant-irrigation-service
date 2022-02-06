package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jdk.jfr.Percentage
import java.time.LocalDateTime

data class IrrigationData(
    val moisturePercentage: Int,
    val dateReceived: LocalDateTime = LocalDateTime.now()
)
