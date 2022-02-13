package com.khnumpottr.plantirrigationservice.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.mongo.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


class MoistureLevelsHandler @Autowired constructor(private val service: MoistureLevelService) : TextWebSocketHandler() {
    val moistureReadingDAO = MoistureReadingDAO()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        service.addWebSocketSession(session)
        val messages = service.reportInitialMoistureLevel()
        messages.forEach{
            session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(it)))
        }
        LOG.info { "Client Connected" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        service.removeWebSocketSession(session)
        LOG.info { "Client Disconnected" }
    }

//    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {}

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

}