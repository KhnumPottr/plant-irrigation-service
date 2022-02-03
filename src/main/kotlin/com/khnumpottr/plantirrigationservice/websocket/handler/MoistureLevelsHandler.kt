package com.khnumpottr.plantirrigationservice.websocket.handler

import com.khnumpottr.plantirrigationservice.dao.mongo.MongoMoistureReadingDAO
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArrayList

class MoistureLevelsHandler : TextWebSocketHandler() {

    val mongo = MongoMoistureReadingDAO()

    var sessions: MutableList<WebSocketSession> = CopyOnWriteArrayList()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        val message = TextMessage("Hello world")
        session.sendMessage(message)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        sessions.remove(session)
    }

//    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {}

}