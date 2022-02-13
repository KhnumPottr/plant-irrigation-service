package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class NodeMessage(
    @JsonProperty("nodeName")
    val nodeName: String,
    @JsonProperty("messageType")
    val messageType: String,
    @JsonProperty("payload")
    val payload: Any,
)
