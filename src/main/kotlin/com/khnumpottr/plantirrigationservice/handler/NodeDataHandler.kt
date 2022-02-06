package com.khnumpottr.plantirrigationservice.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.lang.Integer.parseInt
import java.util.concurrent.CopyOnWriteArrayList

class NodeDataHandler @Autowired constructor(private val service: MoistureLevelService) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        service.addWebSocketSession(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        service.removeWebSocketSession(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val data = ObjectMapper().readValue<MessageData>(message.payload)
        when(MessageTypes.get(data.messageType)){
            MessageTypes.NEW_NODE -> {
                LOG.info { "New node connected ${data.nodeName} - ${session.localAddress}" }
            }
            MessageTypes.DATA -> {
                service.reportMoistureLevel(data)
            }
            MessageTypes.UNKNOWN -> {
                LOG.error { "Message payload unknown, unable to process" }
            }
        }
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

}