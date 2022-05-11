package com.khnumpottr.plantirrigationservice.domain

import java.lang.Integer.parseInt
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class PlanterDetails(
    val planterId: String,
    val title: String? = null,
    val datePlanted: LocalDate? = null,
    val upperLimit: Int? = null,
    val lowerLimit: Int? = null,
    val plants: String? = null
) {
    companion object {
        fun buildFromLHM(planterDetailsLHM: LinkedHashMap<String, Any>): PlanterDetails {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            return PlanterDetails(
                planterId = planterDetailsLHM["planterId"]!! as String,
                title = planterDetailsLHM["title"] as String,
                datePlanted = LocalDateTime.parse(planterDetailsLHM["datePlanted"] as String, formatter).toLocalDate(),
                upperLimit = parseInt(planterDetailsLHM["upperLimit"] as String),
                lowerLimit = parseInt(planterDetailsLHM["lowerLimit"] as String),
                plants = planterDetailsLHM["plants"] as String
            )
        }
    }
}
