package com.khnumpottr.plantirrigationservice.domain.enums

import java.util.*

enum class CommandType {
    IRRIGATE,
    WAKE,
    SLEEP;

    companion object {
        fun get(value: String): CommandType = valueOf(value.uppercase(Locale.getDefault()))
    }
}