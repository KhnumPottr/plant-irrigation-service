package com.khnumpottr.plantirrigationservice.domain.enums

enum class MessageTypes {
    DATA,
    ARRAY_DATA,
    NEW_NODE,
    UNKNOWN;

    companion object {
        fun get(value: String): MessageTypes =
            when (value.lowercase()) {
                "data" -> DATA
                "array_data" -> ARRAY_DATA
                "new_node" -> NEW_NODE
                else -> UNKNOWN
            }
    }
}
