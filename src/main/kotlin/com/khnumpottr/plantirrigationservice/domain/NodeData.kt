package com.khnumpottr.plantirrigationservice.domain

import org.bson.codecs.pojo.annotations.BsonProperty
import java.time.LocalDate

data class NodeData(
    val nodeName: String,
    val title: String? = null,
    val datePlanted: LocalDate? = null,
    val upperLimit: Int? = null,
    val lowerLimit: Int? = null,
    val plants: String? = null
)
