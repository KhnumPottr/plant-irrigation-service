package com.khnumpottr.plantirrigationservice.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class IrrigationHandler @Autowired constructor(private val service: MoistureLevelService) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        LOG.info { "Irrigation Controller Connected" }
        service.connectIrrigationSession(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        LOG.info { "Node Disconnected" }
        service.disconnectIrrigationSession()
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val data = MessageData.build(message)
        when (data.messageType) {
            else -> LOG.error { "Message payload unknown, unable to process" }
        }
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }
}