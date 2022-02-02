package com.khnumpottr.plantirrigationservice.websocket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.khnumpottr.plantirrigationservice.domain.DataMessage
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArrayList

class DataHandler : TextWebSocketHandler() {

    var sessions: MutableList<WebSocketSession> = CopyOnWriteArrayList()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        sessions.remove(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val data = ObjectMapper().readValue<DataMessage>(message.payload)
        println(data)
        //TODO(Store Data and push to the frontend)
    }
}