package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import java.time.LocalDateTime

@JsonDeserialize
data class MessageData(
    @JsonProperty("nodeName")
    val nodeName: String,
    @JsonProperty("messageType")
    val messageType: MessageTypes,
    @JsonProperty("payload")
    val payload: Any,
    val dateReceived: LocalDateTime = LocalDateTime.now(),
)