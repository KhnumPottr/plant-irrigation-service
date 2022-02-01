package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class DataMessage(
    @JsonProperty("nodeName")
    val nodeName: String,
    @JsonProperty("messageType")
    val messageType: String,
    @JsonProperty("message")
    val message: String
)
