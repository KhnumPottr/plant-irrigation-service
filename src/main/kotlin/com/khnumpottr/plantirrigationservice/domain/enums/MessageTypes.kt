package com.khnumpottr.plantirrigationservice.domain.enums

enum class MessageTypes {
    DATA,
    ARRAY_DATA,
    NEW_NODE,
    IRRIGATION_ARRAY_DATA,
    SWITCH,
    UNKNOWN;

    companion object {
        fun get(value: String): MessageTypes =
            when (value.lowercase()) {
                "data" -> DATA
                "array_data" -> ARRAY_DATA
                "new_node" -> NEW_NODE
                "irrigation_array_data" -> IRRIGATION_ARRAY_DATA
                "switch" -> SWITCH
                else -> UNKNOWN
            }
    }
}
