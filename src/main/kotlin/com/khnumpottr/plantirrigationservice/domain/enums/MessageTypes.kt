package com.khnumpottr.plantirrigationservice.domain.enums

enum class MessageTypes{
    DATA,
    NEW_NODE,
    UNKNOWN;

    companion object{
        fun get(value: String): MessageTypes =
            when(value.lowercase()){
                "data" -> DATA
                "new_node" -> NEW_NODE
                else -> UNKNOWN
            }
    }
}
