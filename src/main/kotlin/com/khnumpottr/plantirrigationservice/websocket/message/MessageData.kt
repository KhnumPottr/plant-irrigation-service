package com.khnumpottr.plantirrigationservice.websocket.message

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class MessageData(
    @JsonProperty("nodeName")
    val nodeName: String,
    @JsonProperty("messageType")
    val messageType: String,
    @JsonProperty("payload")
    val payload: String
)