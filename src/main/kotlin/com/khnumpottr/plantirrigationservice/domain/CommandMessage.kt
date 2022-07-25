package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.khnumpottr.plantirrigationservice.domain.enums.CommandType
import org.springframework.web.socket.TextMessage
import java.time.LocalDateTime

data class CommandMessage(
    val id: String,
    val commandType: Int,
    val payload: Any?,
    val dateReceived: String = LocalDateTime.now().toString(),
){
    companion object{
        fun build(message: TextMessage): CommandMessage{
            val data = ObjectMapper().readValue<PlanterCommandMessage>(message.payload)
            return CommandMessage(
                id = data.id,
                commandType = CommandType.get(data.commandType).commandNumber,
                payload = data.payload
            )
        }
    }
}