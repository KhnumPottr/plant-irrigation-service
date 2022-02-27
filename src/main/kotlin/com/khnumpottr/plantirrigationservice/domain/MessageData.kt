package com.khnumpottr.plantirrigationservice.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.readValue
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import org.springframework.web.socket.TextMessage
import java.time.LocalDateTime

data class MessageData(
    val nodeName: String,
    val messageType: MessageTypes,
    val payload: Any?,
    val dateReceived: LocalDateTime = LocalDateTime.now(),
){
    companion object{
        fun build(message: TextMessage): MessageData{
            val data = ObjectMapper().readValue<NodeMessage>(message.payload)
            return MessageData(
                nodeName = data.nodeName,
                messageType = MessageTypes.get(data.messageType),
                payload = data.payload
            )
        }
    }
}