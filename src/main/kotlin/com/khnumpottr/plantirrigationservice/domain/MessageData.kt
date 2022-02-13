package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import java.time.LocalDateTime

data class MessageData(
    val nodeName: String,
    val messageType: MessageTypes,
    val payload: Any,
    val dateReceived: LocalDateTime = LocalDateTime.now(),
)