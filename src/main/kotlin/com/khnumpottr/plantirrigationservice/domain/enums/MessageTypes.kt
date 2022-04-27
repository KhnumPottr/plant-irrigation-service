package com.khnumpottr.plantirrigationservice.domain.enums

import java.util.*

enum class MessageTypes {
    // New
    NODE_DATA_REPORT,
    NODE_TRIGGER_IRRIGATION,
    NODE_NEW_CONNECTION,
    PLANTER_DATA,
    UPDATE_PLANTER_DATA,
    IRRIGATION_ARRAY_DATA,
    IRRIGATION_DATA,
    UNKNOWN;

    companion object {
//        fun get(value: String): MessageTypes =
//            when (value.lowercase()) {
//                "data" -> DATA
//                "array_data" -> ARRAY_DATA
//                "new_node" -> NEW_NODE
//                "planter_data" -> PLANTER_DATA
//                "update_planter_data" -> UPDATE_PLANTER_DATA
//                "irrigation_array_data" -> IRRIGATION_ARRAY_DATA
//                "irrigation_data" -> IRRIGATION_DATA
//                "switch" -> SWITCH
//                else -> UNKNOWN
//            }
        fun get(value: String): MessageTypes = valueOf(value.uppercase(Locale.getDefault()))
    }
}
