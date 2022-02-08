package com.khnumpottr.plantirrigationservice.handler

import com.khnumpottr.plantirrigationservice.dao.mongo.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


class MoistureLevelsHandler @Autowired constructor(private val service: MoistureLevelService) : TextWebSocketHandler() {
    val moistureReadingDAO = MoistureReadingDAO()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        service.addWebSocketSession(session)
        val nodes = service.getActiveNodes()
        service.reportInitialMoistureLevel(session.id)
//        val message = TextMessage(jacksonObjectMapper().writeValueAsString(initialMoistureList))
//        println(message)
//        session.sendMessage(message)

        // Create singleton cache system for moisture levels

        // Once
        // Query database and return 120 of the last records

        // Continuously
        // Save/update most recent levels to cache
        // Save moisture levels to database
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        service.removeWebSocketSession(session)
        LOG.info { "Removing Session" }
    }

//    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {}

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

}