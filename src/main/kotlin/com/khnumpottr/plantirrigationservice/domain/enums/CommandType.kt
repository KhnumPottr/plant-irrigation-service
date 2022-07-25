package com.khnumpottr.plantirrigationservice.domain.enums

import java.util.*

enum class CommandType(val commandNumber: Int) {
    IRRIGATE(commandNumber = 1910),
    IRRIGATE_MAX(commandNumber = 1911),
    WAKE(commandNumber = 2001),
    SLEEP(commandNumber = 2002);

    companion object {
        fun get(value: String): CommandType = valueOf(value.uppercase(Locale.getDefault()))
        fun get(value: Int): CommandType = values().first{ it.commandNumber == value }
    }
}