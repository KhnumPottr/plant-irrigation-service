package com.khnumpottr.plantirrigationservice.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import com.khnumpottr.plantirrigationservice.service.ClientReportingService
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


class WebClientHandler @Autowired constructor(private val service: MoistureLevelService) : TextWebSocketHandler() {

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

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        LOG.info { "Getting Message from client" }
        val data = MessageData.build(message)
        LOG.info { data }
        when (data.messageType) {
            MessageTypes.IRRIGATION_ARRAY_DATA -> {
                val message = service.getMoistureLevelHistory(data.nodeName)
                session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(message)))
            }
            MessageTypes.SWITCH -> {
                val toggle = service.irrigationToggle(data.payload as Boolean)
                session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(data.copy(payload = toggle))))
            }
            else -> {
                LOG.error { "Message payload unknown, unable to process" }
            }
        }
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

}