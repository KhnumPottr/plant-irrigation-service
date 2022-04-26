package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import org.springframework.web.socket.TextMessage
import java.time.LocalDateTime

data class MessageData(
    val id: String,
    val messageType: MessageTypes,
    val payload: Any?,
    val dateReceived: LocalDateTime = LocalDateTime.now(),
){
    companion object{
        fun build(message: TextMessage): MessageData{
            val data = ObjectMapper().readValue<NodeMessage>(message.payload)
            return MessageData(
                id = data.id,
                messageType = MessageTypes.get(data.messageType),
                payload = data.payload
            )
        }
    }
}