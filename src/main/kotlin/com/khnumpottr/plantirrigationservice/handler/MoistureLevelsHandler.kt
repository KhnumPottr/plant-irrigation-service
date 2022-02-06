package com.khnumpottr.plantirrigationservice.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.mongo.MongoMoistureReadingDAO
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArrayList


class MoistureLevelsHandler @Autowired constructor(private val service: MoistureLevelService) : TextWebSocketHandler() {

    val mongo = MongoMoistureReadingDAO()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        service.addWebSocketSession(session)
        println(session)
        val initialMoistureList = mongo.findAllMoisture(120)
        val message = TextMessage(jacksonObjectMapper().writeValueAsString(initialMoistureList))
        println(message)
        session.sendMessage(message)

        // Create singleton cache system for moisture levels

        // Once
        // Query database and return 120 of the last records

        // Continuously
        // Save/update most recent levels to cache
        // Save moisture levels to database
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        service.removeWebSocketSession(session)
    }

//    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {}

}