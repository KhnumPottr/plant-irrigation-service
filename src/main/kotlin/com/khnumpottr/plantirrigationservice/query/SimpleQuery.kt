package com.khnumpottr.plantirrigationservice.query

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import java.util.*

@Component
class SimpleQuery : Query {

    fun generateNullableNumber(): Int? {
        val num = Random().nextInt(100)
        return if (num < 50) num else null
    }

    fun generateNumber(): Int = Random().nextInt(100)
}