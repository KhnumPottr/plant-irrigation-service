package com.khnumpottr.plantirrigationservice.domain

import com.khnumpottr.plantirrigationservice.domain.enums.CommandType
import java.time.LocalDateTime

data class Command(
    val planterId: String,
    val issuedCommand: CommandType,
    val instigated: Boolean = false,
    val dateReceived: LocalDateTime = LocalDateTime.now(),
)
