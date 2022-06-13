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
    COMMAND,
    COMMAND_REQUEST,
    UNKNOWN;

    companion object {
        fun get(value: String): MessageTypes = valueOf(value.uppercase(Locale.getDefault()))
    }
}
