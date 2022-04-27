package com.khnumpottr.plantirrigationservice.handler

import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class NodeDataHandler @Autowired constructor(private val service: MoistureLevelService) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        LOG.info { "Node Connected" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        LOG.info { "Node Disconnected" }
        service.removeDataNode(session.id)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val data = MessageData.build(message)
        when (data.messageType) {
            MessageTypes.NODE_NEW_CONNECTION -> {
                LOG.info { "New node connected ${data.id} - ${session.localAddress}" }
                service.addDataNode(data.id, session.id)
            }
            MessageTypes.NODE_DATA_REPORT -> {
                service.reportMoistureLevel(session.id,data)
                service.irrigationThresholdCheck(session)
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