package com.khnumpottr.plantirrigationservice.domain.enums

enum class MessageTypes {
    DATA,
    ARRAY_DATA,
    NEW_NODE,
    PLANTER_DATA,
    UPDATE_PLANTER_DATA,
    IRRIGATION_ARRAY_DATA,
    IRRIGATION_DATA,
    SWITCH,
    UNKNOWN;

    companion object {
        fun get(value: String): MessageTypes =
            when (value.lowercase()) {
                "data" -> DATA
                "array_data" -> ARRAY_DATA
                "new_node" -> NEW_NODE
                "planter_data" -> PLANTER_DATA
                "update_planter_data" -> UPDATE_PLANTER_DATA
                "irrigation_array_data" -> IRRIGATION_ARRAY_DATA
                "irrigation_data" -> IRRIGATION_DATA
                "switch" -> SWITCH
                else -> UNKNOWN
            }
    }
}
