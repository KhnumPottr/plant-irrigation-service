package com.khnumpottr.plantirrigationservice.websocket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.khnumpottr.plantirrigationservice.dao.mongo.MongoMoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.lang.Integer.parseInt
import java.util.concurrent.CopyOnWriteArrayList

class NodeDataHandler : TextWebSocketHandler() {

    val mongo = MongoMoistureReadingDAO()

    var sessions: MutableList<WebSocketSession> = CopyOnWriteArrayList()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        sessions.remove(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val data = ObjectMapper().readValue<MessageData>(message.payload)
        mongo.insert(IrrigationData(data.nodeName, parseInt(data.payload)))
        //TODO(push to the frontend)
    }
}