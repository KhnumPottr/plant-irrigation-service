package com.khnumpottr.plantirrigationservice.domain

data class NodeMoistureLevels(
    val nodeName: String,
    val levels: List<IrrigationData>
)