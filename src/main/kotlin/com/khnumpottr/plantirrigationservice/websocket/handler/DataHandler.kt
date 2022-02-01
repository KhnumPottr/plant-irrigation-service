package com.khnumpottr.plantirrigationservice.websocket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.khnumpottr.plantirrigationservice.domain.DataMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class DataHandler : TextWebSocketHandler() {

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val data = ObjectMapper().readValue<DataMessage>(message.payload)
        println(data)
        //TODO(Store Data and push to the frontend)
    }
}