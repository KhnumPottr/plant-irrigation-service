package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class PlanterCommandMessage(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("commandType")
    val commandType: String,
    @JsonProperty("payload")
    val payload: Any?,
)
